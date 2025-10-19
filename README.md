# 🎮 League: LoL In-House Match Management System

## 📘 Overview
**League** is a web platform built to manage *in-house League of Legends* matches, currently used by over **70 active players**.  
The system allows users to **register, log match history, view performance statistics**, and leverage an **automatic team matching algorithm** to ensure balanced gameplay.

---

## 🧩 Features
- 🧑‍🤝‍🧑 **User Registration & Management**  
  Manage user profiles, summoner names, and team affiliations.
- 🎮 **Match Record Logging**  
  Record match outcomes and player statistics through a custom admin dashboard.
- 📈 **Performance & Statistics Dashboard**  
  Track aggregated user win rates, total games played, and ranking metrics.
- ⚙️ **Automatic Team Matching**  
  Matchmaking algorithm dynamically creates balanced teams based on player stats.
- 🔒 **Admin Control Panel**  
  Moderators can review and edit game history, verify results, and manage user access.

---

## 🧱 Tech Stack
| Layer | Technology |
|--------|-------------|
| **Backend** | Java Spring Framework |
| **Infrastructure** | AWS (EC2, RDS, Route53) |
| **Containerization** | Docker |
| **Database** | MySQL |
| **Version Control / CI** | GitHub |

---

## ⚙️ System Architecture

---

## 🚀 Deployment
- Deployed on **AWS EC2** instance with **Docker containerization**
- Managed domain via **AWS Route53**
- Database hosted on **AWS RDS (MySQL)**  
- Configured continuous deployment via GitHub repository sync

---

## 🧮 Example Use Case
1. A player registers an account on the website  
2. Admin logs the player’s game results  
3. The system updates statistics and recalculates ranking metrics  
4. Before the next match, the automatic team matcher assigns balanced rosters based on historical data

---

## 📊 Key Statistics
| Metric | Value |
|---------|--------|
| Registered Users | 70+ |
| Games Recorded | 200+ |
| Average Active Users / Week | ~40 |

---
