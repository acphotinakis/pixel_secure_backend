"""
Secure mock data generator:
- Field-level encryption (Fernet) for PII
- PBKDF2 password hashing with per-password salt
- Uses secrets for token/id generation
- Two-pass follow assignment
- Noise injection for playtime (privacy)
- Writes pretty-printed table files to OUTPUT_DIR
- Normalized collections for MongoDB
"""

import random
import argparse
import secrets
from faker import Faker
from typing import List
from bson import ObjectId
from utils.data_gen import *
from utils.mongo import *
from utils.output import *

# ---------- Configuration ----------
NUM_USERS = 50
NUM_GAMES = 30
NUM_CONTRIBUTORS = 40
NUM_PLATFORMS = 5
NUM_GENRES = 10

MAX_COLLECTIONS_PER_USER = 3
MAX_GAMES_PER_COLLECTION = 5
MAX_FOLLOWS_PER_USER = 10
MAX_PLATFORMS_PER_USER = 2
MAX_PLAYTIME_PER_GAME = 50

PLATFORMS = [
    "Steam",
    "Nintendo eStore",
    "PlayStation Store",
    "Xbox Store",
    "Epic Games Store",
    "GOG",
    "Android PlayStore",
    "iOS AppStore",
    "Origin",
    "Ubisoft Connect",
]

GENRES = [
    "Action",
    "Adventure",
    "RPG",
    "FPS",
    "Puzzle",
    "Platformer",
    "Strategy",
    "Sports",
    "Simulation",
    "Horror",
    "MMO",
    "Racing",
    "Fighting",
    "Sandbox",
    "Survival",
]

ESRB_RATINGS = ["E", "E10+", "T", "M", "A", "RP"]

fake = Faker()


# ---------- Generators ----------
def generate_platforms(platform_names: list):
    return [{"_id": ObjectId(), "platform_name": name} for name in platform_names]


def generate_genres(genre_names: list):
    return [{"_id": ObjectId(), "genre_name": name} for name in genre_names]


def generate_contributors(n: int):
    contributors = []
    for _ in range(n):
        contributors.append(
            {
                "_id": ObjectId(),
                "contributor_name": fake.company(),
                "type": random.choice(["developer", "publisher"]),
            }
        )
    return contributors


def generate_videogames(n: int, contributors: list, genres: list, platforms: list):
    games = []
    dev_ids = [c["_id"] for c in contributors if c["type"] == "developer"]
    pub_ids = [c["_id"] for c in contributors if c["type"] == "publisher"]
    genre_ids = [g["_id"] for g in genres]

    for _ in range(n):
        num_devs = min(len(dev_ids), random.randint(1, 2)) if dev_ids else 0
        num_pubs = min(len(pub_ids), random.randint(1, 2)) if pub_ids else 0
        num_genres = random.randint(1, min(3, len(genres)))

        games.append(
            {
                "_id": ObjectId(),
                "title": fake.catch_phrase().replace(".", ""),
                "esrb": random.choice(ESRB_RATINGS),
                "developers": random.sample(dev_ids, k=num_devs) if num_devs else [],
                "publishers": random.sample(pub_ids, k=num_pubs) if num_pubs else [],
                "genres": random.sample(genre_ids, num_genres),
            }
        )

    return games


def generate_platform_releases(games: list, platforms: list):
    platform_releases = []
    platform_name_to_id = {p["platform_name"]: p["_id"] for p in platforms}

    for game in games:
        # Assign 1-3 random platforms per game
        selected_platforms = random.sample(
            platforms, random.randint(1, min(3, len(platforms)))
        )
        for platform in selected_platforms:
            platform_releases.append(
                {
                    "_id": ObjectId(),
                    "game_id": game["_id"],
                    "platform_id": platform["_id"],
                    "price": round(random.uniform(4.99, 79.99), 2),
                    "releaseDate": str(
                        fake.date_time_between(
                            start_date="-10y", end_date="now"
                        ).isoformat()
                    ),
                }
            )

    return platform_releases


def generate_users(n: int, games: list, platforms: list):
    users = []
    owned_collection = []
    plays_collection = []
    ratings_collection = []
    access_times_collection = []
    follows_collection = []
    plain_user_data_for_testing = []

    for _ in range(n):
        # --- Plain fields ---
        username_plain = fake.user_name()
        email_plain = fake.email()
        fname_plain = fake.first_name()
        lname_plain = fake.last_name()
        password_plain = fake.password(length=20)

        # --- Derived security fields ---
        pw_record = hash_password(password_plain)
        email_masked = email_plain.split("@")[0][:3] + "***@" + fake.free_email_domain()

        # --- Shared values ---
        user_id = ObjectId()
        creation_date = str(
            fake.date_time_between(start_date="-5y", end_date="now").isoformat()
        )
        platforms_assigned = random.sample(
            platforms, k=random.randint(1, min(MAX_PLATFORMS_PER_USER, len(platforms)))
        )
        audit_token = secrets.token_urlsafe(16)

        # --- Encrypted user object ---
        user_doc = {
            "_id": user_id,
            "username_enc": encrypt_field(username_plain),
            "username_hash": hash_username(username_plain),
            "email_enc": encrypt_field(email_plain),
            "email_masked": email_masked,
            "firstName_enc": encrypt_field(fname_plain),
            "lastName_enc": encrypt_field(lname_plain),
            "password_hash": pw_record["hash"],
            "password_salt": pw_record["salt"],
            "creationDate": creation_date,
            "role": "USER",
            "platforms": platforms_assigned,
            "audit_token": audit_token,
        }
        users.append(user_doc)

        # --- Plain + encrypted for testing ---
        plain_user_data_for_testing.append(
            {
                "_id": user_id,
                "username_plain": username_plain,
                "email_plain": email_plain,
                "fname_plain": fname_plain,
                "lname_plain": lname_plain,
                "password_plain": password_plain,
                **user_doc,
            }
        )

        # Owned games and derived plays/ratings
        owned_games_count = random.randint(1, min(5, len(games)))
        owned_game_choices = random.sample(games, k=owned_games_count)
        for g in owned_game_choices:
            owned_collection.append(
                {
                    "_id": ObjectId(),
                    "user_id": user_id,
                    "game_id": g["_id"],
                    "acquisitionDate": str(
                        fake.date_time_between(
                            start_date="-3y", end_date="now"
                        ).isoformat()
                    ),
                }
            )
            # Play sessions
            for _ in range(random.randint(1, 3)):
                base_hours = random.randint(1, MAX_PLAYTIME_PER_GAME)
                noisy_hours = max(1, base_hours + int(random.gauss(0, 2)))
                plays_collection.append(
                    {
                        "_id": ObjectId(),
                        "user_id": user_id,
                        "game_id": g["_id"],
                        "datetimeOpened": str(
                            fake.date_time_between(
                                start_date="-2y", end_date="now"
                            ).isoformat()
                        ),
                        "timePlayed": noisy_hours * 3600,
                    }
                )
            # Ratings
            if random.random() < 0.8:
                ratings_collection.append(
                    {
                        "_id": ObjectId(),
                        "user_id": user_id,
                        "game_id": g["_id"],
                        "rating": random.randint(1, 5),
                        "ratingDate": str(
                            fake.date_time_between(
                                start_date="-1y", end_date="now"
                            ).isoformat()
                        ),
                    }
                )

        # Access timestamps
        for _ in range(random.randint(5, 20)):
            access_times_collection.append(
                {
                    "_id": ObjectId(),
                    "user_id": user_id,
                    "time": str(
                        fake.date_time_between(
                            start_date="-2y", end_date="now"
                        ).isoformat()
                    ),
                }
            )

    # Follows relationships
    all_user_ids = [u["_id"] for u in users]
    for u in users:
        max_followable = min(MAX_FOLLOWS_PER_USER, len(all_user_ids) - 1)
        follow_count = random.randint(0, max_followable)
        candidates = [uid for uid in all_user_ids if uid != u["_id"]]
        follow_ids = (
            secrets.SystemRandom().sample(candidates, k=follow_count)
            if follow_count > 0
            else []
        )
        for fid in follow_ids:
            follows_collection.append(
                {"_id": ObjectId(), "follower_id": u["_id"], "followed_id": fid}
            )

    return (
        users,
        owned_collection,
        plays_collection,
        ratings_collection,
        access_times_collection,
        follows_collection,
        plain_user_data_for_testing,
    )


def generate_collections(users: list, games: list):
    collections = []
    counter = 1
    game_ids = [g["_id"] for g in games]
    for u in users:
        for _ in range(random.randint(0, MAX_COLLECTIONS_PER_USER)):
            k = (
                random.randint(1, min(MAX_GAMES_PER_COLLECTION, len(game_ids)))
                if game_ids
                else 0
            )
            collections.append(
                {
                    "_id": ObjectId(),
                    "name": fake.word().capitalize() + " Collection",
                    "description": fake.sentence(),
                    "user_id": u["_id"],
                    "games": secrets.SystemRandom().sample(game_ids, k=k) if k else [],
                }
            )
            counter += 1
    return collections


def generate_all_data():
    platforms = generate_platforms(PLATFORMS)
    genres = generate_genres(GENRES)
    contributors = generate_contributors(NUM_CONTRIBUTORS)
    games = generate_videogames(NUM_GAMES, contributors, genres, platforms)
    platform_releases = generate_platform_releases(games, platforms)
    users, owned, plays, ratings, access_times, follows, plain_user_data_for_testing = (
        generate_users(NUM_USERS, games, platforms)
    )
    collections = generate_collections(users, games)

    return {
        "platforms": platforms,
        "genres": genres,
        "contributors": contributors,
        "videogames": games,
        "platform_releases": platform_releases,
        "users": users,
        "plain_user_data_for_testing": plain_user_data_for_testing,
        "owned": owned,
        "plays": plays,
        "ratings": ratings,
        "access_times": access_times,
        "follows": follows,
        "collections": collections,
    }


# ---------- Main ----------
def main():
    parser = argparse.ArgumentParser(
        description="Generate secure videogame dataset, save CSVs, and optionally upload to MongoDB."
    )

    parser.add_argument(
        "--data-output-dir",
        type=str,
        default="secure_output_data_power",
        help="Directory where generated CSV files will be written (default: ./secure_output_data_power).",
    )
    parser.add_argument(
        "--drop-database",
        action="store_true",
        help="Drop the MongoDB database before uploading (start fresh).",
    )
    parser.add_argument(
        "--upload-data-to-mongo",
        action="store_true",
        help="Upload all generated collections to MongoDB.",
    )
    parser.add_argument(
        "--skip-csv",
        action="store_true",
        help="Skip writing CSVs to disk (only generate in-memory data).",
    )
    parser.add_argument(
        "--no-indexes",
        action="store_true",
        help="Do not create MongoDB indexes after uploading.",
    )
    parser.add_argument(
        "--schema-sample-size",
        type=int,
        default=2,
        help="Number of documents to sample when printing MongoDB schema (default: 2).",
    )

    args = parser.parse_args()
    print(vars(args))  # Debug: show CLI args

    # --- Step 1: Connect DB if needed ---
    db = None
    if args.upload_data_to_mongo:
        db = mongodb_ping_and_handle(drop_db=args.drop_database)
        if db is None:
            print("❌ Could not connect to MongoDB. Exiting.")
            sys.exit(1)

    # --- Step 2: Generate all synthetic data ---
    data = generate_all_data()

    # --- Step 3: Save CSVs if not skipped ---
    if not args.skip_csv:
        save_csvs_if_requested(args.data_output_dir, data)

    # --- Step 4: Upload to MongoDB if requested ---
    if args.upload_data_to_mongo and db is not None:
        upload_to_mongo_if_requested(db, data, args.no_indexes, args.schema_sample_size)

    # --- Final status ---
    print("\n🎉 Data generation pipeline complete.")
    if args.upload_data_to_mongo:
        print("📤 Data uploaded to MongoDB.")
    if not args.skip_csv:
        print(f"📂 Data CSVs available at: {args.data_output_dir}")


if __name__ == "__main__":
    main()
