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
from pymongo import MongoClient
from pymongo.server_api import ServerApi
import os
import random
import json
import csv
import secrets
import hashlib
import base64
from datetime import datetime
from faker import Faker
from cryptography.fernet import Fernet
from typing import List
from bson import ObjectId
from dotenv import load_dotenv
import os

# ---------- Configuration ----------
OUTPUT_DIR = "secure_output_data_power"
os.makedirs(OUTPUT_DIR, exist_ok=True)

NUM_USERS = 50
NUM_GAMES = 30
NUM_CONTRIBUTORS = 40
NUM_PLATFORMS = 5
NUM_GENRES = 10

MAX_COLLECTIONS_PER_USER = 3
MAX_GAMES_PER_COLLECTION = 5
MAX_FOLLOWS_PER_USER = 10
MAX_PLATFORMS_PER_USER = 2
MAX_PLAYTIME_PER_GAME = 50  # hours (base cap before noise)

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

# MongoDB connection
load_dotenv()
MONGO_URI = os.getenv("MONGO_URI")

if MONGO_URI is None:
    raise ValueError("MONGO_URI is not set in the environment.")
else:
    print("MONGO_URI is set in the environment.")

client = MongoClient(MONGO_URI, server_api=ServerApi("1"))
# Ping to confirm connection
try:
    client.admin.command("ping")
    client.server_info()
    print("Pinged your deployment. Successfully connected to MongoDB!")
except Exception as e:
    print("Connection failed:", e)

# Drop the entire database to start fresh
db_name = "game_db"
if db_name in client.list_database_names():
    client.drop_database(db_name)
    print(f"⚠️ Dropped existing database '{db_name}' to start fresh.")

db = client[db_name]

# ---------- Security helpers ----------
ENCRYPTION_KEY = os.getenv("ENCRYPTION_KEY")
if ENCRYPTION_KEY is None:
    raise ValueError("ENCRYPTION_KEY is not set in the environment.")
else:
    print("ENCRYPTION_KEY is set in the environment.")
fernet = Fernet(ENCRYPTION_KEY.encode())


def encrypt_field(plaintext: str) -> str:
    """Encrypt a string and return URL-safe base64 text (Fernet token)."""
    if plaintext is None:
        return ""
    return fernet.encrypt(plaintext.encode()).decode()


def decrypt_field(token: str) -> str:
    """Decrypt a Fernet token (for backend reference)."""
    if not token:
        return ""
    return fernet.decrypt(token.encode()).decode()


def mask_email(email: str) -> str:
    """Return a masked email for display (not reversible)."""
    try:
        local, domain = email.split("@", 1)
        if len(local) <= 1:
            local_mask = "*"
        else:
            local_mask = local[0] + ("*" * max(1, len(local) - 2)) + local[-1]
        return f"{local_mask}@{domain}"
    except Exception:
        return "***@***"


def hash_password(password: str, iterations: int = 310_000) -> dict:
    """Hash password with PBKDF2-HMAC-SHA256. Return dict with Base64 salt+hash."""
    salt = secrets.token_bytes(16)
    dk = hashlib.pbkdf2_hmac("sha256", password.encode(), salt, iterations, dklen=32)
    return {
        "salt": base64.b64encode(salt).decode(),  # Base64 instead of hex
        "hash": base64.b64encode(dk).decode(),  # Base64 instead of hex
        "iterations": iterations,
    }


def verify_password(
    password: str, salt_b64: str, hash_b64: str, iterations: int
) -> bool:
    """Verify a password against stored Base64 salt/hash."""
    salt = base64.b64decode(salt_b64)
    dk = hashlib.pbkdf2_hmac("sha256", password.encode(), salt, iterations, dklen=32)
    return base64.b64encode(dk).decode() == hash_b64


# Use faker
fake = Faker()


# ---------- Pretty print writer ----------
def save_pretty_table(filename: str, data: List[dict], fieldnames: List[str]) -> None:
    """Save list of dicts to a pretty-printed, aligned text-based CSV"""
    filepath = os.path.join(OUTPUT_DIR, filename)
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    # Compute column widths
    col_widths = {field: len(field) for field in fieldnames}
    for row in data:
        for field in fieldnames:
            col_widths[field] = max(col_widths[field], len(str(row.get(field, ""))))

    with open(filepath, "w", encoding="utf-8") as f:
        # Write header
        header = " | ".join(field.ljust(col_widths[field]) for field in fieldnames)
        f.write(header + "\n")
        f.write("-+-".join("-" * col_widths[field] for field in fieldnames) + "\n")

        # Write rows
        for row in data:
            line = " | ".join(
                str(row.get(field, "")).ljust(col_widths[field]) for field in fieldnames
            )
            f.write(line + "\n")

    print(f"Saved pretty-printed table to {filepath}")


def upload_to_mongo(db, collection_name, data):
    """Insert documents into MongoDB collection"""
    if not data:
        print(f"⚠️ No data for {collection_name}, skipping insert.")
        return
    collection = db[collection_name]
    collection.insert_many(data)
    print(f"📤 Inserted {len(data)} documents into '{collection_name}' collection.")


# ---------- Generators ----------
def generate_contributors(n: int):
    contributors = []
    for _ in range(n):
        contributors.append(
            {
                "contributor_id": ObjectId(),
                "name_enc": encrypt_field(fake.company()),
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
                "_id": f"game_{i+1}",
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
                                fake.date_between(start_date="-10y", end_date="today")
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
        uid = f"user_{i+1}"
        username_plain = fake.user_name()
        email_plain = fake.email()
        fname_plain = fake.first_name()
        lname_plain = fake.last_name()
        password_plain = fake.password(length=12)

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
                    "vgId": g["_id"],
                    "acquisitionDate": str(
                        fake.date_between(start_date="-3y", end_date="now")
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
                        "vgId": g["_id"],
                        "datetimeOpened": str(
                            fake.date_time_between(start_date="-2y", end_date="now")
                        ),
                        "timePlayed": noisy_hours * 3600,  # seconds
                    }
                )
            # maybe rate it
            if random.random() < 0.8:
                ratings.append(
                    {
                        "vgId": g["_id"],
                        "rating": random.randint(1, 5),
                        "ratingDate": str(
                            fake.date_between(start_date="-1y", end_date="now")
                        ),
                    }
                )

        access = [
            {"time": str(fake.date_time_between(start_date="-2y", end_date="now"))}
            for _ in range(random.randint(5, 20))
        ]

        users.append(
            {
                "_id": uid,  # pseudonymous stable id used for relationships
                # Encrypted PII fields (backend must decrypt)
                "username_enc": encrypt_field(username_plain),
                "email_enc": encrypt_field(email_plain),
                "email_masked": mask_email(
                    email_plain
                ),  # safe masked display, not reversible
                "firstName_enc": encrypt_field(fname_plain),
                "lastName_enc": encrypt_field(lname_plain),
                # Password: store PBKDF2 salt/hash (backend can verify)
                "password_hash": pw_record["hash"],
                "password_salt": pw_record["salt"],
                "password_iterations": pw_record["iterations"],
                # Role and metadata
                "creationDate": str(
                    fake.date_time_between(start_date="-5y", end_date="now")
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
                    "_id": f"collection_{counter}",
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


# ---------- Auditing (simple) ----------
def write_audit_log(events: List[dict]):
    """Save a simple audit log of generation events (not PII)."""
    filename = os.path.join(OUTPUT_DIR, "audit_log.csv")
    # Very small pretty CSV
    fieldnames = ["timestamp", "event", "details"]
    # append
    write_header = not os.path.exists(filename)
    with open(filename, "a", encoding="utf-8") as f:
        if write_header:
            f.write(",".join(fieldnames) + "\n")
        for ev in events:
            row = [
                ev.get("timestamp", ""),
                ev.get("event", ""),
                json.dumps(ev.get("details", "")),
            ]
            f.write(",".join('"' + c.replace('"', '""') + '"' for c in row) + "\n")


# ---------- Main ----------
def main():
    # Step 1: select platforms and genres (trimmed lists)
    platforms = PLATFORMS[:NUM_PLATFORMS]
    genres = GENRES[:NUM_GENRES]

    # Step 2: contributors
    contributors = generate_contributors(NUM_CONTRIBUTORS)
    # Step 3: videogames
    games = generate_videogames(NUM_GAMES, contributors, genres, platforms)
    # Step 4: users
    users = generate_users(NUM_USERS, games, platforms)
    # Step 5: collections
    collections = generate_collections(users, games)

    # Save pretty tables
    save_pretty_table("platforms.csv", [{"_id": p} for p in platforms], ["_id"])
    save_pretty_table("genres.csv", [{"_id": g} for g in genres], ["_id"])
    save_pretty_table("contributors.csv", contributors, ["_id", "name_enc", "type"])
    save_pretty_table(
        "videogames.csv",
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
        "collections.csv", collections, ["_id", "name", "description", "user", "games"]
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

    # --- Upload to MongoDB (keep native lists/dicts) ---
    upload_to_mongo(db, "platforms", [{"_id": p} for p in platforms])
    upload_to_mongo(db, "genres", [{"_id": g} for g in genres])
    upload_to_mongo(db, "contributors", contributors)
    upload_to_mongo(db, "videogames", games)
    upload_to_mongo(db, "users", users)
    upload_to_mongo(db, "collections", collections)


if __name__ == "__main__":
    main()
