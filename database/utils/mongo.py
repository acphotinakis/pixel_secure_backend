import sys
from pymongo import MongoClient
from pymongo.server_api import ServerApi
import certifi
from collections import defaultdict
from dotenv import load_dotenv
import os

# MongoDB connection
load_dotenv()
MONGO_URI = os.getenv("MONGO_URI")

if MONGO_URI is None:
    raise ValueError("MONGO_URI is not set in the environment.")
else:
    print("MONGO_URI is set in the environment.")


# ---------- MONGO ----------
def mongodb_ping_and_handle(drop_db=False):
    client = MongoClient(MONGO_URI, server_api=ServerApi("1"))
    try:
        # Ping to confirm connection
        client.admin.command("ping")
        print("Pinged your deployment. Successfully connected to MongoDB!")

        # Drop the entire database to start fresh
        db_name = "game_db"
        if drop_db and db_name in client.list_database_names():
            client.drop_database(db_name)
            print(f"⚠️ Dropped existing database '{db_name}' to start fresh.")

        db = client[db_name]
        return db

    except Exception as e:
        print(f"Connection failed: {e}")


def create_mongo_indexes(db):
    """
    Adds indexes to MongoDB collections to improve query performance.
    Adjust fields per collection based on expected query patterns.
    """

    # USERS: commonly queried by username/email and for joins
    db.users.create_index("username_enc", unique=True)
    db.users.create_index("email_enc", unique=True)
    db.users.create_index("audit_token")

    # GAMES: often queried by title or developer/publisher/genre
    db.videogames.create_index("title")
    db.videogames.create_index("developers")
    db.videogames.create_index("publishers")
    db.videogames.create_index("genres")

    # CONTRIBUTORS: lookups by name or type
    db.contributors.create_index("contributor_name")
    db.contributors.create_index("type")

    # PLATFORMS: lookups by platform_name
    db.platforms.create_index("platform_name", unique=True)

    # GENRES: lookups by genre_name
    db.genres.create_index("genre_name", unique=True)

    # PLATFORM RELEASES: queries by game or platform
    db.platformReleases.create_index("game_id")
    db.platformReleases.create_index("platform_id")

    # OWNED GAMES: queries by user_id or game_id
    db.owned.create_index("user_id")
    db.owned.create_index("game_id")

    # PLAYS: queries by user/game combinations
    db.plays.create_index([("user_id", 1), ("game_id", 1)])
    db.plays.create_index("datetimeOpened")

    # RATINGS: queries by user/game
    db.ratings.create_index([("user_id", 1), ("game_id", 1)])
    db.ratings.create_index("ratingDate")

    # ACCESS TIMES: queries by user
    db.accessTimes.create_index("user_id")
    db.accessTimes.create_index("time")

    # FOLLOWS: follower/followed lookups
    db.follows.create_index("follower_id")
    db.follows.create_index("followed_id")

    # COLLECTIONS: queries by user
    db.collections.create_index("user_id")
    db.collections.create_index("collection_id")

    print("✅ Indexes created for all collections.")


def upload_to_mongo_if_requested(db, data, no_indexes, schema_sample_size):
    upload_all_data_tables_to_mongo(
        db,
        data["platforms"],
        data["genres"],
        data["contributors"],
        data["videogames"],
        data["platform_releases"],
        data["users"],
        data["owned"],
        data["plays"],
        data["ratings"],
        data["access_times"],
        data["follows"],
        data["collections"],
    )

    if not no_indexes:
        create_mongo_indexes(db)

    print_db_schema(db, sample_size=schema_sample_size)


def upload_all_data_tables_to_mongo(
    db,
    platforms,
    genres,
    contributors,
    games,
    platform_releases,
    users,
    owned,
    plays,
    ratings,
    access_times,
    follows,
    collections,
):
    # Upload normalized collections
    upload_to_mongo(db, "platforms", platforms)
    upload_to_mongo(db, "genres", genres)
    upload_to_mongo(db, "contributors", contributors)
    upload_to_mongo(db, "videogames", games)
    upload_to_mongo(db, "platformReleases", platform_releases)
    upload_to_mongo(db, "users", users)
    upload_to_mongo(db, "owned", owned)
    upload_to_mongo(db, "plays", plays)
    upload_to_mongo(db, "ratings", ratings)
    upload_to_mongo(db, "accessTimes", access_times)
    upload_to_mongo(db, "follows", follows)
    upload_to_mongo(db, "collections", collections)


# ---------- Mongo Utilities ----------
def upload_to_mongo(db, collection_name, data):
    """Insert documents into MongoDB collection"""
    if not data:
        print(f"⚠️ No data for {collection_name}, skipping insert.")
        return
    collection = db[collection_name]
    collection.insert_many(data)
    print(f"📤 Inserted {len(data)} documents into '{collection_name}' collection.")


def print_db_schema(db, sample_size=50):
    """
    Prints the schema of every collection in the given MongoDB database.
    Infers schema by sampling up to `sample_size` documents per collection.
    """
    for collection_name in db.list_collection_names():
        print(f"\n📂 Collection: {collection_name}")
        schema = defaultdict(set)
        collection = db[collection_name]

        # Sample documents to infer schema
        cursor = collection.find({}, limit=sample_size)
        for doc in cursor:
            for key, value in doc.items():
                schema[key].add(type(value).__name__)

        if not schema:
            print("   ⚠️ No documents found (empty collection).")
        else:
            for field, types in schema.items():
                print(f"   • {field}: {', '.join(types)}")
