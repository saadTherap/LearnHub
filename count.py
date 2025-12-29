import os
import collections

# --- CONFIGURATION ---
TARGET_DIRECTORY = "./cloned_repo"  # <--- Change this to your directory path

SUPPORTED_EXTENSIONS = {
    ".py", ".java", ".js", ".ts", ".go", ".c", ".cpp", ".h", ".hpp",
    ".cs", ".rb", ".php", ".swift", ".kt", ".scala",
    ".md", ".txt", ".rst", ".html", ".css", ".scss",
    ".yaml", ".yml", ".json", ".xml", ".proto", "Dockerfile", ".sh",
    ".tf", ".tfvars", ".bicep", ".gradle", "pom.xml", "requirements.txt",
    "package.json", "go.mod", "go.sum", "Cargo.toml"
}

def count_supported_files(directory):
    total_count = 0
    extension_counts = collections.defaultdict(int)
    
    print(f"📂 Scanning directory: {os.path.abspath(directory)} ...\n")

    if not os.path.exists(directory):
        print(f"❌ Error: Directory '{directory}' not found.")
        return

    for root, _, files in os.walk(directory):
        # Optional: Skip .git folder to speed up and avoid counting internals
        if '.git' in root:
            continue
            
        for filename in files:
            # Check for exact filename matches (e.g., Dockerfile, pom.xml)
            if filename in SUPPORTED_EXTENSIONS:
                total_count += 1
                extension_counts[filename] += 1
                continue

            # Check for extension matches (e.g., .py, .java)
            # We get the extension including the dot
            _, ext = os.path.splitext(filename)
            if ext in SUPPORTED_EXTENSIONS:
                total_count += 1
                extension_counts[ext] += 1

    # --- REPORTING ---
    print("-" * 40)
    print(f"✅ Total Supported Files Found: {total_count}")
    print("-" * 40)
    
    if total_count > 0:
        print("Breakdown by type:")
        # Sort by count descending
        sorted_counts = sorted(extension_counts.items(), key=lambda item: item[1], reverse=True)
        for ext, count in sorted_counts:
            print(f"  {ext:<15} : {count}")
    else:
        print("No matching files found.")

if __name__ == "__main__":
    count_supported_files(TARGET_DIRECTORY)