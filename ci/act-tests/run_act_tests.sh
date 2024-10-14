#!/bin/bash
set -e

# Check if Act is installed
if ! command -v act &> /dev/null; then
    echo "Error: Act is not installed. Please install Act to run the tests."
    echo "You can find installation instructions at: https://github.com/nektos/act#installation"
    exit 1
fi

# Get the root directory of the Git repository
REPO_ROOT=$(git rev-parse --show-toplevel)

# Get the directory of the script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Initialize counters
total_tests=$(ls "$SCRIPT_DIR"/*.json | wc -l)
successful_tests=0

# Run tests for each JSON 'events' file
for test_file in "$SCRIPT_DIR"/*.json; do
    echo ">>>>>>>>>> Running test with $test_file"

    if act pull_request --eventpath "$test_file" -W "$REPO_ROOT/.github/workflows" -j 'validate' ; then
        echo ">>>>>>>>>> Test passed: $test_file"
        successful_tests=$((successful_tests + 1))  # Increment successful tests count
    else
        echo ">>>>>>>>>> Test failed: $test_file"
    fi
done

# Summary of results
echo "=============================="
echo "Total tests run: $total_tests"
echo "Successful tests: $successful_tests"

# Check if all tests passed
if [[ $successful_tests -eq $total_tests ]]; then
    echo "All tests passed."
else
    echo "$((total_tests - successful_tests)) test(s) failed."
    exit 1
fi
