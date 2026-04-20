# WORKFLOW.md - Development Workflow for bookworm-backend

## 📋 Overview
This document outlines the complete development workflow for contributing to the bookworm-backend repository, considering the branch protection rules in place.

## 🔒 Branch Protection Rules
The `test` branch has the following protections:
1. **No direct pushes** - All changes must go through Pull Requests
2. **Required status checks** (3 checks must pass):
   - `build` - Build must succeed
   - `unit-tests` - All unit tests must pass
   - `integration-tests` - All integration tests must pass
3. **Additional protections**:
   - Admins must follow rules too
   - No force pushes allowed
   - No branch deletion allowed

## 🚀 Complete Workflow for New Features/Changes

### Step 1: Start from the latest test branch
```bash
# Ensure you have the latest changes
git checkout test
git pull origin test

# Create a new feature branch with descriptive name
git checkout -b feature/descriptive-name
# Examples:
# - feature/add-user-authentication
# - fix/resolve-null-pointer-exception
# - docs/update-api-documentation
```

### Step 2: Make your changes
```bash
# Make your code changes
# Stage changes
git add .

# Commit with conventional commit message
git commit -m "feat: Add user authentication system"
# Commit types: feat, fix, docs, style, refactor, test, chore
```

### Step 3: Push your branch
```bash
# Push to remote
git push origin feature/descriptive-name
```

### Step 4: Create Pull Request
```bash
# Create PR from your branch to test
gh pr create \
  --base test \
  --head feature/descriptive-name \
  --title "feat: Add user authentication system" \
  --body "## Description
Detailed description of changes...

## Changes Made
- Added JWT authentication
- Created user service
- Updated API endpoints

## Testing
- [x] Unit tests added
- [x] Integration tests passing
- [x] Manual testing completed"
```

### Step 5: Wait for CI/CD Checks
The following checks will run automatically:
1. ✅ **build** - Project builds successfully
2. ✅ **unit-tests** - All unit tests pass
3. ✅ **integration-tests** - All integration tests pass

Monitor the PR page for check status.

### Step 6: Address Review Comments
If reviewers request changes:
```bash
# Make requested changes
git add .
git commit -m "fix: Address review comments"
git push origin feature/descriptive-name
```

### Step 7: Merge the PR
Once all checks pass and PR is approved:
```bash
# Merge and delete branch (optional)
gh pr merge <PR_NUMBER> --squash --delete-branch

# Or merge from GitHub UI
```

### Step 8: Update local branches
```bash
# Switch to test and pull latest
git checkout test
git pull origin test

# Delete local feature branch (optional)
git branch -d feature/descriptive-name
```

## 🔄 Syncing test with main
Periodically, `test` should be synced with `main`:

### Option A: From test to main (when test is ahead)
```bash
# Create PR from test to main
gh pr create \
  --base main \
  --head test \
  --title "Sync: Merge test changes into main" \
  --body "Synchronizing test branch with main including latest features and fixes."
```

### Option B: From main to test (when main has hotfixes)
```bash
# Create PR from main to test
gh pr create \
  --base test \
  --head main \
  --title "Hotfix: Sync main fixes to test" \
  --body "Bringing hotfixes from main into test branch."
```

## 🐛 Common Issues & Solutions

### Error: "Protected branch update failed"
```
remote: error: GH006: Protected branch update failed for refs/heads/test.
remote: - Changes must be made through a pull request.
```
**Solution**: Follow the workflow above - create a feature branch and PR.

### Error: "Required status checks are expected"
**Solution**: Wait for CI/CD checks to complete or check why they're failing.

### Working with multiple features
```bash
# Stash current work if needed
git stash

# Switch to test for new feature
git checkout test
git pull origin test
git checkout -b feature/another-feature

# Later, return to stashed work
git checkout feature/first-feature
git stash pop
```

## 📝 Commit Message Convention
Use conventional commits:
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `style:` Code style changes (formatting, etc.)
- `refactor:` Code refactoring
- `test:` Adding or updating tests
- `chore:` Maintenance tasks

## 🏷️ Branch Naming Convention
- `feature/` - New features
- `fix/` - Bug fixes
- `hotfix/` - Urgent production fixes
- `docs/` - Documentation updates
- `refactor/` - Code refactoring
- `test/` - Test-related changes

## 🔧 Useful Commands

### Check branch status
```bash
git status
git log --oneline --graph --all --decorate -10
```

### See what's different
```bash
# Compare local test with remote
git log --oneline origin/test..test

# Compare test with main
git log --oneline origin/main..origin/test
```

### Clean up branches
```bash
# Delete local merged branches
git branch --merged | grep -v "\*" | xargs -n 1 git branch -d

# Delete remote branch after PR merge
git push origin --delete feature/branch-name
```

## 📊 Visual Workflow
```
[Local Machine]                  [GitHub]
     |                              |
1. git checkout test               |
2. git pull origin test            |
3. git checkout -b feature/xxx     |
4. Make changes & commit           |
5. git push origin feature/xxx     |--> Creates remote branch
6. gh pr create                    |--> Creates PR #XX
     |                              |--> CI/CD runs checks
     |                              |--> Reviewers comment
7. Address feedback                |
8. git push origin feature/xxx     |--> Updates PR
     |                              |--> Checks re-run
     |                              |--> PR approved & merged
9. git checkout test               |
10. git pull origin test           |--> test updated
11. git branch -d feature/xxx      |
```

## 🚨 Important Notes
1. **Never push directly to `test`** - Always use PRs
2. **Ensure all checks pass** before merging
3. **Keep PRs focused** - One feature/fix per PR
4. **Write descriptive commit messages** - Follow conventional commits
5. **Update documentation** when changing APIs or behavior

## 🤝 Team Collaboration
- Assign PRs to appropriate reviewers
- Use @mentions for specific team members
- Add labels to categorize PRs (bug, enhancement, documentation, etc.)
- Link issues to PRs using `Closes #123` or `Fixes #456` in PR description

---
*Last updated: 2024-01-15*
*Repository: bookworm-backend*
*Protected branches: test*