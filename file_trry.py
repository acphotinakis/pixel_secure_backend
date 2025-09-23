import sys
import os
import re

BACKEND_CODE_DIR_PATH = "backend/src/main/java/com/videogamedb/app"

# ANNOTATION_DIR = "backend/src/main/java/com/videogamedb/app/annotation"
# AUDIT_DIR = "backend/src/main/java/com/videogamedb/app/audit"
# CONFIG_DIR = "backend/src/main/java/com/videogamedb/app/config"
# CONTROLLERS_DIR = "backend/src/main/java/com/videogamedb/app/controllers"
# DTO_DIR = "backend/src/main/java/com/videogamedb/app/dto"
# EXCEPTIONS_DIR = "backend/src/main/java/com/videogamedb/app/exceptions"
# MODELS_DIR = "backend/src/main/java/com/videogamedb/app/models"
# PAYLOAD_DIR = "backend/src/main/java/com/videogamedb/app/payload"
# REPOSITORIES_DIR = "backend/src/main/java/com/videogamedb/app/repositories"
# SECURITY_DIR = "backend/src/main/java/com/videogamedb/app/security"
SERVICES_DIR = "backend/src/main/java/com/videogamedb/app/services"
# UTIL_DIR = "backend/src/main/java/com/videogamedb/app/util"
APP_RESOURCES_DIR_PATH = "backend/src/main/resources"

DATABSE_CODE_DIR_PATH = "database"


def save_files_contents(output_file: str, paths: list[str]):
    """
    Reads contents of files and all files in given directories, writes names and contents to an output file.
    Replaces getters and setters with a comment.
    """
    with open(output_file, "w", encoding="utf-8") as out_f:
        for path in paths:
            if not os.path.exists(path):
                print(f"⚠️ Path not found: {path}")
                continue

            # Helper to process a single file
            def process_file(file_path):
                try:
                    with open(file_path, "r", encoding="utf-8") as f:
                        content = f.read()

                    # Pattern to match getter and setter methods
                    # This matches: public <return_type> get<Name>() { ... } or public void set<Name>(...) { ... }
                    getter_setter_pattern = r"^\s*public\s+(?:[\w<>\[\]]+\s+get\w+|void\s+set\w+)\s*\([^)]*\)\s*\{[^}]*\}"

                    # Use re.MULTILINE and re.DOTALL to handle multi-line methods
                    filtered_content = re.sub(
                        getter_setter_pattern,
                        "",
                        content,
                        flags=re.MULTILINE | re.DOTALL,
                    )

                    # Now remove any consecutive blank lines that might result from the removal
                    filtered_content = re.sub(r"\n\s*\n\s*\n", "\n\n", filtered_content)

                    # Find where to insert the "Assume Getters and setters here" comment
                    # Look for the constructors section or end of class
                    lines = filtered_content.split("\n")
                    output_lines = []
                    in_constructor_section = False
                    constructor_section_found = False
                    getter_comment_inserted = False

                    for i, line in enumerate(lines):
                        # Check if we're in or after constructors section
                        if (
                            "// Constructors" in line
                            or "Constructors" in line
                            and "//" in line
                        ):
                            in_constructor_section = True
                            constructor_section_found = True
                            output_lines.append(line)
                        elif in_constructor_section and line.strip() == "":
                            # Found empty line after constructors - insert getters comment here
                            if (
                                not getter_comment_inserted
                                and constructor_section_found
                            ):
                                output_lines.append(
                                    "\t// Assume Getters and setters here"
                                )
                                output_lines.append("")
                                getter_comment_inserted = True
                                in_constructor_section = False
                            else:
                                output_lines.append(line)
                        elif (
                            constructor_section_found
                            and not getter_comment_inserted
                            and line.strip().startswith("}")
                        ):
                            # If we reached the end of class without finding a good spot, insert before closing brace
                            output_lines.append("\t// Assume Getters and setters here")
                            output_lines.append(line)
                            getter_comment_inserted = True
                        else:
                            output_lines.append(line)

                    # If we never found a constructors section but there are fields, insert after last field
                    if not getter_comment_inserted:
                        output_lines = []
                        in_fields_section = True
                        field_pattern = re.compile(
                            r"^\s*(private|protected|public)\s+[\w<>\[\]]+\s+\w+;"
                        )
                        last_field_index = -1

                        for i, line in enumerate(lines):
                            if field_pattern.match(line.strip()):
                                last_field_index = i
                            output_lines.append(line)

                        if last_field_index != -1:
                            # Insert after the last field
                            output_lines.insert(
                                last_field_index + 1,
                                "\t// Assume Getters and setters here",
                            )
                            output_lines.insert(last_field_index + 2, "")

                    final_content = "\n".join(output_lines)

                    out_f.write(f"--- {file_path} ---\n")
                    out_f.write(final_content + "\n\n")
                except Exception as e:
                    print(f"⚠️ Failed to read file {file_path}: {e}")

            # If it's a file, process it
            if os.path.isfile(path):
                process_file(path)

            # If it's a directory, process all files recursively
            elif os.path.isdir(path):
                for root, _, files in os.walk(path):
                    for filename in files:
                        file_path = os.path.join(root, filename)
                        process_file(file_path)

    print(f"✅ All file contents saved to {output_file}")


# paths = [
#     CONFIG_DIR,
#     CONTROLLERS_DIR + "/AuthController.java",
#     CONTROLLERS_DIR + "/UserController.java",
#     DTO_DIR,
#     MODELS_DIR + "/User.java",
#     PAYLOAD_DIR,
#     REPOSITORIES_DIR + "/UserRepository.java",
#     SECURITY_DIR,
#     SERVICES_DIR + "/UserService.java",
#     UTIL_DIR,
# ]
paths = [SERVICES_DIR, APP_RESOURCES_DIR_PATH]

save_files_contents("fssile_rety_data_output.txt", paths)

# paths = ["video-game-cli/src/main/java/com/videogamedb/cli"]

# save_files_contents("sall_code_output.txt", paths)

# paths = [CONTROLLERS_DIR]

# save_files_contents("controllers_oput.txt", paths)
