# Git Cheat Sheet 

This cheat sheet covers common Git commands for collaborative development. Use it as a quick reference for essential Git tasks.

---

## **1. Cloning a Repository**
```bash
# Clone a repository (replace URL with the repository link)
git clone <repository-url>
```

---

## **2. Pulling Changes**
```bash
# Pull the latest changes from the remote repository
git pull origin <branch-name>

# Example: Pull changes from the main branch
git pull origin main
```

---

## **3. Committing Changes**

### **Stages and Commits**
```bash
# Stage changes (specific file)
git add <file-name>

# Stage all changes
git add .

# Commit staged changes with a message
git commit -m "Your commit message"
```

---

## **4. Pushing Changes**
```bash
# Push changes to the remote repository
git push origin <branch-name>

# Example: Push changes to the main branch
git push origin main
```

---

## **5. Branching**

### **Create a New Branch Locally**
```bash
# Create a new branch
git branch <branch-name>

# Switch to the new branch
git checkout <branch-name>

# Shortcut: Create and switch to a new branch
git checkout -b <branch-name>
```

### **Attach Local Branch to Remote**
```bash
# Push the new branch and set upstream tracking
git push -u origin <branch-name>
```

---

## **6. Checking Branch Status**
```bash
# List all branches (local and remote)
git branch -a

# Show the current branch
git branch

```

---

## **7. Resolving Merge Conflicts**

### **Steps to Resolve**
1. Open the conflicting files.
2. Manually resolve conflicts marked with `<<<<<<`, `======`, and `>>>>>>`.
3. Stage the resolved files:
   ```bash
   git add <file-name>
   ```
4. Commit the resolution:
   ```bash
   git commit -m "Resolved merge conflict"
   ```

---

## **8. Viewing Repository Status**
```bash
# Check the current status of the repository
git status
```

---

## **9. Deleting Branches**

### **Delete Local Branch**
```bash
# Delete a local branch
git branch -d <branch-name>
```

### **Delete Remote Branch**
```bash
# Delete a remote branch
git push origin --delete <branch-name>
```

---

## **10. Helpful Tips**

### **Check Remote Repositories**
```bash
# View remote repositories
git remote -v
```

## Notes from my experience using Git and GitHub collaboratively for the first time: 
1. Solving merge conflicts for the first time can be very confusing, myself or Misha can help with that if needed.
2. When a branch is resolved (has completed a PR and is merged with the main branch), it is good practice to delete it and start a new branch for your next work.
3. Commit very frequently, even if all you did was fix a small bug or add a small feature. Also ensure commit messages are descriptive of what was done. 
4. Push commits to the remote branch when finished working (in most cases), as it essentially acts as an extra save, just in case something gets lost on your own machine.
5. Remember when running any Git commands in your terminal that the directory holding the repository on your local machine is open. If you run the commands in VSCode terminal, it usually opens to the correct directory by default but I know Eclipse does not do this.  

---

