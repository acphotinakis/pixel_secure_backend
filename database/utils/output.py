from typing import List
import csv
import json
import os
import sys
import shutil
from datetime import datetime


# ---------- Auditing (simple) ----------
def write_audit_log(output_dir: str, events: List[dict]):
    """Save a simple audit log of generation events (not PII)."""
    filename = os.path.join(output_dir, "audit_log.csv")
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


# ---------- Pretty print writer ----------
def save_pretty_table(output_dir: str, filename: str, data: List[dict]) -> None:
    """Save list of dicts to a pretty-printed, aligned text-based CSV with inferred fieldnames"""
    if not data:
        print(f"⚠️ No data to save for {filename}")
        return

    # infer fieldnames from the first row
    fieldnames = list(data[0].keys())

    filepath = os.path.join(output_dir, filename)
    os.makedirs(output_dir, exist_ok=True)

    # compute column widths
    col_widths = {field: len(field) for field in fieldnames}
    for row in data:
        for field in fieldnames:
            col_widths[field] = max(col_widths[field], len(str(row.get(field, ""))))

    with open(filepath, "w", encoding="utf-8") as f:
        # header
        header = " | ".join(field.ljust(col_widths[field]) for field in fieldnames)
        f.write(header + "\n")
        f.write("-+-".join("-" * col_widths[field] for field in fieldnames) + "\n")

        # rows
        for row in data:
            line = " | ".join(
                str(row.get(field, "")).ljust(col_widths[field]) for field in fieldnames
            )
            f.write(line + "\n")

    print(f"Saved pretty-printed table to {filepath}")


def save_all_csvs(output_dir, **tables):
    # Clear output dir
    if os.path.exists(output_dir):
        shutil.rmtree(output_dir)
    os.makedirs(output_dir, exist_ok=True)

    # Save each table
    for name, data in tables.items():
        save_pretty_table(output_dir, f"{name}.csv", data)

    # Audit log
    events = [
        {
            "timestamp": str(datetime.utcnow()),
            "event": "GENERATE",
            "details": f"Generated {len(tables.get('users', []))} users, "
            f"{len(tables.get('videogames', []))} games, "
            f"{len(tables.get('contributors', []))} contributors",
        }
    ]
    write_audit_log(output_dir, events)


def save_csvs_if_requested(output_dir, data):
    save_all_csvs(
        output_dir,
        platforms=data["platforms"],
        genres=data["genres"],
        contributors=data["contributors"],
        videogames=data["videogames"],
        platformreleases=data["platform_releases"],
        users=data["users"],
        plain_user_data_for_testing=data["plain_user_data_for_testing"],
        owned=data["owned"],
        plays=data["plays"],
        ratings=data["ratings"],
        access_times=data["access_times"],
        follows=data["follows"],
        collections=data["collections"],
    )
    print(f"✅ CSVs saved to {output_dir}")
