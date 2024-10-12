# Act Test Suite

This repository includes a test suite designed to validate the GitHub Actions workflows using **Act**. The primary purpose of these tests is to document the project's chosen Git branching strategy, which follows the [Git Flow](https://www.gitkraken.com/learn/git/git-flow) model. Additionally, these tests provide a way for developers to locally verify the CI rules governing the branching policy.

## Purpose

The tests serve two main functions:

1. **Documentation**: They act as practical documentation of the Git Flow branching strategy implemented in this project. By running these tests, developers can understand the required naming conventions for branches.

2. **Local Testing**: Developers can execute these tests locally to validate that any changes to the branching policy or CI rules are functioning as intended before they are merged into the main codebase.

## Branching Strategy

This project utilises the Git Flow branching model, which includes the following branch types:

- **Main**: The production branch containing stable code.
- **Develop**: The branch where ongoing development occurs.
- **Feature**: Branches created off of `develop` for new features.
- **Release**: Branches created to prepare for production releases.
- **Hotfix**: Branches created from `main` for urgent fixes.

## Prerequisites

- Ensure that you have [Docker](https://www.docker.com/get-started/) installed and running on your machine.
- Install **Act**. You can find the installation instructions [here](https://github.com/nektos/act#installation).

## Test Files

The test suite contains the following JSON files in the `ci/act-tests` directory. Each file represents a different scenario for testing the rules defined in the branching policy:

- `bugfix_pr_to_develop.json`: Simulates a pull request from a bugfix branch to the `develop` branch.
- `chore_pr_to_develop.json`: Simulates a pull request from a chore branch to the `develop` branch.
- `chore_pr_to_main.json`: Simulates a pull request from a chore branch to the `main` branch.
- `chore_pr_to_release.json`: Simulates a pull request from a chore branch to a release branch.
- `feature_pr_to_develop.json`: Simulates a pull request from a feature branch to the `develop` branch.
- `hotfix_pr_to_main.json`: Simulates a pull request from a hotfix branch to the `main` branch.
- `hotfix_pr_to_release.json`: Simulates a pull request from a hotfix branch to a release branch.

## Running the Tests

To run the test suite, follow these steps:

1. **Navigate to the Repository Root**:
   Open your terminal and change to the root directory of the repository:

   ```bash
   cd /path/to/your/repo
