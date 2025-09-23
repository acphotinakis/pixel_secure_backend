import os
import shutil


def copy_and_convert_to_txt(input_dir, output_dir):
    """
    Recursively copies a directory structure and converts all files into .txt files.
    File contents are preserved as-is (binary files will still be written).
    """
    if not os.path.exists(input_dir):
        raise FileNotFoundError(f"Input directory '{input_dir}' does not exist.")

    for root, dirs, files in os.walk(input_dir):
        # Build corresponding output directory path
        relative_path = os.path.relpath(root, input_dir)
        target_dir = os.path.join(output_dir, relative_path)
        os.makedirs(target_dir, exist_ok=True)

        for file in files:
            src_file = os.path.join(root, file)

            # Keep original name but append .txt
            new_name = file + ".txt"
            dst_file = os.path.join(target_dir, new_name)

            try:
                with open(src_file, "rb") as f_in:
                    data = f_in.read()
                # Try decode as utf-8, otherwise keep binary as str repr
                try:
                    text = data.decode("utf-8", errors="replace")
                except Exception:
                    text = str(data)

                with open(dst_file, "w", encoding="utf-8") as f_out:
                    f_out.write(text)
            except Exception as e:
                print(f"⚠️ Failed to convert {src_file}: {e}")


if __name__ == "__main__":

    input_dir = "src"
    output_dir = "txt_output"
    copy_and_convert_to_txt(input_dir, output_dir)
    print(f"✅ Converted copy created at '{output_dir}'")
