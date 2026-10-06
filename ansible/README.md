# Week 13: Configuration Management with Ansible

## Configuration Specification
**Target Node OS:** Ubuntu 20.04/22.04 LTS (Linux)
**Prerequisites Addressed:**
1. **Packages:** `apt-transport-https`, `ca-certificates`, `curl`, `software-properties-common`, `ufw`
2. **Services:** Docker Engine (Community Edition)
3. **Users:** A dedicated application user named `canteen_user` (added to the `docker` group)
4. **Folders:** `/opt/canteen-token-system` for application deployments and logs
5. **Ports & Firewall:** UFW enabled, opening TCP port `8082` for the Spring Boot application.

## How to Execute the Playbook

Since Ansible requires a Linux/Unix control node to run, you can execute this playbook using WSL (Windows Subsystem for Linux), a Linux Virtual Machine, or a GitHub Actions runner.

### Step 1: Install Ansible (on WSL/Linux)
```bash
sudo apt update
sudo apt install -y ansible
```

### Step 2: Configure the Inventory
Open the `inventory.ini` file and change the `192.168.1.100` IP address to the actual IP of your target server/VM. Ensure you have SSH access to that server.

### Step 3: Run the Playbook
Execute the playbook against your target node:
```bash
ansible-playbook -i inventory.ini playbook.yml
```

### Example Execution Log (For Deliverable)
When you run the command above, you will see an execution log similar to this:
```text
PLAY [Provision Canteen Token System Target Node] ******************************

TASK [Gathering Facts] *********************************************************
ok: [target_node]

TASK [Update APT package cache] ************************************************
changed: [target_node]

TASK [Install prerequisite packages] *******************************************
changed: [target_node]

TASK [Add Docker GPG apt Key] **************************************************
changed: [target_node]

TASK [Add Docker APT repository] ***********************************************
changed: [target_node]

TASK [Install Docker Engine] ***************************************************
changed: [target_node]

TASK [Ensure Docker service is running and enabled] ****************************
ok: [target_node]

TASK [Create dedicated application user] ***************************************
changed: [target_node]

TASK [Create application deployment directory] *********************************
changed: [target_node]

TASK [Allow application port through firewall] *********************************
changed: [target_node]

TASK [Enable UFW firewall] *****************************************************
changed: [target_node]

PLAY RECAP *********************************************************************
target_node                : ok=11   changed=9    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0
```
