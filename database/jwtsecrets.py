# simple_jwt_secret_generator.py
import secrets
import base64


def generate_jwt_secrets(count=4, length=64):
    """Simple function to generate JWT secrets"""
    secrets_list = []
    for i in range(count):
        # Generate random bytes and encode as base64
        random_bytes = secrets.token_bytes(length)
        secret = base64.urlsafe_b64encode(random_bytes).decode("ascii").rstrip("=")
        secrets_list.append(secret)

    return secrets_list


# Generate 4 secrets for your Spring Boot application
secrets = generate_jwt_secrets(4, 64)
secrets_string = ",".join(secrets)

print("Add this to your application.properties:")
print(f"app.jwtSecrets={secrets_string}")

print("\nOr to your application.yml:")
print("app:")
print(f"  jwtSecrets: {secrets_string}")
