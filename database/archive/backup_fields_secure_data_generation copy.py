"""
Secure mock data generator:
- Field-level encryption (Fernet) for PII
- PBKDF2 password hashing with per-password salt
- Uses secrets for token/id generation
- Two-pass follow assignment
- Noise injection for playtime (privacy)
- Writes pretty-printed table files to OUTPUT_DIR
"""

import sys
import os
import random
import json
import secrets
from datetime import datetime
from faker import Faker
from typing import List
from bson import ObjectId
from faker import Faker
from utils import *

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

OUTPUT_DIR = "secure_output_data_power"


# ---------- Generators ----------
def generate_platforms(platform_names: list):
    platforms = []
    for i in range(len(platform_names)):
        platforms.append(
            {
                "_id": ObjectId(),
                "platform_name": platform_names[i],
            }
        )
    return platforms


def generate_genres(genre_names: list):
    genres = []
    for i in range(len(genre_names)):
        genres.append(
            {
                "_id": ObjectId(),
                "genre_name": genre_names[i],
            }
        )
    return genres


def generate_contributors(n: int):
    contributors = []
    for i in range(n):
        contributors.append(
            {
                "_id": ObjectId(),
                "contributor_name": encrypt_field(fake.company()),
                "type": random.choice(["developer", "publisher"]),
            }
        )
    return contributors


def generate_videogames(n: int, contributors: list, genres: list, platforms: list):
    games = []
    # Pre-split contributor ids by type for realistic assignment
    dev_ids = [c["_id"] for c in contributors if c["type"] == "developer"]
    pub_ids = [c["_id"] for c in contributors if c["type"] == "publisher"]
    for i in range(n):
        # Random but safe sampling
        num_devs = min(len(dev_ids), random.randint(1, 2)) if dev_ids else 0
        num_pubs = min(len(pub_ids), random.randint(1, 2)) if pub_ids else 0
        num_genres = random.randint(1, min(3, len(genres)))
        selected_platforms = random.sample(
            platforms, random.randint(1, min(3, len(platforms)))
        )

        games.append(
            {
                "_id": ObjectId(),
                "title": fake.catch_phrase().replace(".", ""),
                "esrb": random.choice(ESRB_RATINGS),
                "developers": (
                    json.dumps(random.sample(dev_ids, k=num_devs))
                    if num_devs
                    else json.dumps([])
                ),
                "publishers": (
                    json.dumps(random.sample(pub_ids, k=num_pubs))
                    if num_pubs
                    else json.dumps([])
                ),
                "genres": json.dumps(random.sample(genres, num_genres)),
                "platformReleases": json.dumps(
                    [
                        {
                            "platformName": p,
                            "price": round(random.uniform(4.99, 79.99), 2),
                            "releaseDate": str(
                                fake.date_between(
                                    start_date="-10y", end_date="today"
                                ).isoformat()
                            ),
                        }
                        for p in selected_platforms
                    ]
                ),
            }
        )
    return games


def generate_users(n: int, games: list, platforms: list):
    users = []
    # First pass: create pseudonymous users (IDs) + encrypted PII, hashed password
    for i in range(n):
        username_plain = fake.user_name()
        email_plain = fake.email()
        fname_plain = fake.first_name()
        lname_plain = fake.last_name()
        password_plain = fake.password(length=20)

        pw_record = hash_password(password_plain)
        # Owned games, plays & ratings will be created referencing game _ids below
        owned = []
        plays = []
        ratings = []
        # each user gets 1-5 owned games (random)
        owned_games_count = random.randint(1, min(5, len(games)))
        owned_game_choices = random.sample(games, k=owned_games_count) if games else []

        for g in owned_game_choices:
            owned.append(
                {
                    "game_id": g["_id"],
                    "acquisitionDate": str(
                        fake.date_between(start_date="-3y", end_date="now").isoformat()
                    ),
                }
            )
            # for each owned game, create 1-3 play sessions
            for _ in range(random.randint(1, 3)):
                # Inject Gaussian noise into playtime (privacy)
                base_hours = random.randint(1, MAX_PLAYTIME_PER_GAME)
                noisy_hours = max(
                    0, base_hours + int(random.gauss(0, 2))
                )  # mean 0, stddev 2 hours
                plays.append(
                    {
                        "game_id": g["_id"],
                        "datetimeOpened": str(
                            fake.date_time_between(
                                start_date="-2y", end_date="now"
                            ).isoformat()
                        ),
                        "dsf": fake.user,
                        "timePlayed": noisy_hours * 3600,
                    }
                )
            # maybe rate it
            if random.random() < 0.8:
                ratings.append(
                    {
                        "game_id": g["_id"],
                        "rating": random.randint(1, 5),
                        "ratingDate": str(
                            fake.date_between(
                                start_date="-1y", end_date="now"
                            ).isoformat()
                        ),
                    }
                )

        access = [
            {
                "time": str(
                    fake.date_time_between(start_date="-2y", end_date="now").isoformat()
                )
            }
            for _ in range(random.randint(5, 20))
        ]

        users.append(
            {
                "_id": ObjectId(),
                # Encrypted PII fields (backend must decrypt)
                "username_enc": encrypt_field(username_plain),
                "email_enc": encrypt_field(email_plain),
                "email_masked": email_plain.split("@")[0][:3]
                + "***@"
                + fake.free_email_domain(),
                "firstName_enc": encrypt_field(fname_plain),
                "lastName_enc": encrypt_field(lname_plain),
                # Password: store PBKDF2 salt/hash (backend can verify)
                "password_hash": pw_record["hash"],
                "password_salt": pw_record["salt"],
                "password_iterations": pw_record["iterations"],
                # Role and metadata
                "creationDate": str(
                    fake.date_time_between(start_date="-5y", end_date="now").isoformat()
                ),
                "role": "USER",
                # Embedded arrays stored as JSON strings for table export
                "accessDatetimes": json.dumps(access),
                "ownedGames": json.dumps(owned),
                "plays": json.dumps(plays),
                "ratings": json.dumps(ratings),
                "platforms": json.dumps(
                    random.sample(
                        platforms,
                        k=random.randint(
                            1, min(MAX_PLATFORMS_PER_USER, len(platforms))
                        ),
                    )
                ),
                # follows to be filled in second pass (list of _ids)
                "follows": json.dumps([]),
                # Audit token for this user generation (secure random)
                "audit_token": secrets.token_urlsafe(16),
            }
        )

    # Second pass: assign follows referencing real user _ids
    all_user_ids = [u["_id"] for u in users]
    for u in users:
        max_followable = min(MAX_FOLLOWS_PER_USER, len(all_user_ids) - 1)
        follow_count = random.randint(0, max_followable)
        candidates = [uid for uid in all_user_ids if uid != u["_id"]]
        follows = (
            secrets.SystemRandom().sample(candidates, k=follow_count)
            if follow_count > 0
            else []
        )
        u["follows"] = json.dumps(follows)

    return users


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
                    "collection_id": f"collection_{counter}",
                    "name": fake.word().capitalize() + " Collection",
                    "description": fake.sentence(),
                    "user": u["_id"],  # pseudonymous user id
                    "games": json.dumps(
                        secrets.SystemRandom().sample(game_ids, k=k) if k else []
                    ),
                }
            )
            counter += 1
    return collections


# ---------- Main ----------
def main():
    # Step 1: select platforms and genres (trimmed lists)
    platforms = generate_platforms(PLATFORMS[:NUM_PLATFORMS])
    genres = generate_genres(GENRES[:NUM_GENRES])

    # Step 2: contributors
    contributors = generate_contributors(NUM_CONTRIBUTORS)
    # Step 3: videogames
    games = generate_videogames(NUM_GAMES, contributors, genres, platforms)
    # Step 4: users
    users = generate_users(NUM_USERS, games, platforms)
    # Step 5: collections
    collections = generate_collections(users, games)

    os.makedirs(OUTPUT_DIR, exist_ok=True)

    # Save pretty tables
    save_pretty_table(
        OUTPUT_DIR, "platforms.csv", [{"_id": p} for p in platforms], ["_id"]
    )
    save_pretty_table(OUTPUT_DIR, "genres.csv", [{"_id": g} for g in genres], ["_id"])
    save_pretty_table(
        OUTPUT_DIR, "contributors.csv", contributors, ["_id", "name_enc", "type"]
    )
    save_pretty_table(
        "OUTPUT_DIR,videogames.csv",
        games,
        [
            "_id",
            "title",
            "esrb",
            "developers",
            "publishers",
            "genres",
            "platformReleases",
        ],
    )
    save_pretty_table(
        OUTPUT_DIR,
        "users.csv",
        users,
        [
            "_id",
            "username_enc",
            "email_masked",
            "email_enc",
            "firstName_enc",
            "lastName_enc",
            "password_hash",
            "password_salt",
            "password_iterations",
            "creationDate",
            "role",
            "accessDatetimes",
            "ownedGames",
            "plays",
            "ratings",
            "platforms",
            "follows",
            "audit_token",
        ],
    )
    save_pretty_table(
        OUTPUT_DIR,
        "collections.csv",
        collections,
        ["_id", "name", "description", "user", "games"],
    )

    # Audit events
    events = [
        {
            "timestamp": str(datetime.utcnow()),
            "event": "GENERATE",
            "details": f"Generated {len(users)} users, {len(games)} games, {len(contributors)} contributors",
        }
    ]
    write_audit_log(events)

    print(f"✅ Secure data generation complete. Output directory: {OUTPUT_DIR}")
    print(
        "⚠️ Remember: the Spring Boot backend must have the same ENCRYPTION_KEY to decrypt encrypted fields."
    )
    print(
        " - Encrypted fields: username_enc, email_enc, firstName_enc, lastName_enc (Fernet)"
    )
    print(
        " - Passwords are PBKDF2 hashed (store salt + iterations + hash). Backend must verify via PBKDF2."
    )

    db = mongodb_ping_and_handle()

    # --- Upload to MongoDB (keep native lists/dicts) ---
    upload_to_mongo(db, "platforms", [{"_id": p} for p in platforms])
    upload_to_mongo(db, "genres", [{"_id": g} for g in genres])
    upload_to_mongo(db, "contributors", contributors)
    upload_to_mongo(db, "videogames", games)
    upload_to_mongo(db, "users", users)
    upload_to_mongo(db, "collections", collections)


if __name__ == "__main__":
    main()
