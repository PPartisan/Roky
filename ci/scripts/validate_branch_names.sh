#!/bin/bash
set -e

branch_name="$1"
base_branch="$2"

# Define constants for messages
TARGET_BRANCH_MESSAGE="This workflow only checks branches targeting 'main', 'develop', or release branches. Current target branch: '${base_branch}'."
CHORE_BRANCH_MESSAGE="Chore branches are allowed for both 'main' and 'develop'."
MAIN_BRANCH_ERROR_MESSAGE="For branches targeting 'main', use 'hotfix/(ticket_number)_some-text'. Current branch: '${branch_name}'."
DEVELOP_BRANCH_ERROR_MESSAGE="For branches targeting 'develop', use 'feature/(ticket_number)_some-text' or 'bugfix/(ticket_number)_some-text'. Current branch: '${branch_name}'."
HOTFIX_MERGE_ERROR_MESSAGE="Only 'hotfix' branches are allowed to merge into release branches. Current branch: '${branch_name}'."

# Early exit if the target branch is not 'main', 'develop', or a release branch
if [[ "$base_branch" != "main" && "$base_branch" != "develop" && "$base_branch" != release/* ]]; then
    echo "$TARGET_BRANCH_MESSAGE"
    exit 0
fi

# Early exit if the branch is a chore branch
if [[ "$branch_name" == chore/* ]]; then
    echo "$CHORE_BRANCH_MESSAGE"
    exit 0
fi

# Define branch patterns
main_branch_pattern="^(hotfix/[0-9]+_.+)$"
develop_branch_pattern="^(feature/[0-9]+_.+|bugfix/[0-9]+_.+)$"
release_branch_pattern="^(hotfix/[0-9]+_.+)$"

# Check for 'main' branch
if [[ "$base_branch" == "main" ]] && [[ ! "$branch_name" =~ $main_branch_pattern ]]; then
    echo "$MAIN_BRANCH_ERROR_MESSAGE"
    exit 1
# Check for 'develop' branch
elif [[ "$base_branch" == "develop" ]] && [[ ! "$branch_name" =~ $develop_branch_pattern ]]; then
    echo "$DEVELOP_BRANCH_ERROR_MESSAGE"
    exit 1
# Check for release branches
elif [[ "$base_branch" == release/* ]] && [[ ! "$branch_name" =~ $release_branch_pattern ]]; then
    echo "$HOTFIX_MERGE_ERROR_MESSAGE"
    exit 1
fi
