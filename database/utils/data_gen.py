import sys
from cryptography.fernet import Fernet
import secrets
import hashlib
import base64
from dotenv import load_dotenv
import os

# ---------- Security helpers ----------
load_dotenv()
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


def hash_password(password: str, iterations: int = 310_000) -> dict:
    """Hash password with PBKDF2-HMAC-SHA256. Return dict with Base64 salt+hash."""
    salt = secrets.token_bytes(16)
    dk = hashlib.pbkdf2_hmac("sha256", password.encode(), salt, iterations, dklen=32)
    return {
        "salt": base64.b64encode(salt).decode(),
        "hash": base64.b64encode(dk).decode(),
    }


def verify_password(
    password: str, salt_b64: str, hash_b64: str, iterations: int
) -> bool:
    """Verify a password against stored Base64 salt/hash."""
    salt = base64.b64decode(salt_b64)
    dk = hashlib.pbkdf2_hmac("sha256", password.encode(), salt, iterations, dklen=32)
    return base64.b64encode(dk).decode() == hash_b64


def hash_username(plaintext: str) -> str:
    digest = hashlib.sha256(plaintext.encode()).digest()
    return base64.b64encode(digest).decode()
