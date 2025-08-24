# VirtualItemVault

A lightweight Paper plugin that lets players **deposit** a configured item into a **virtual balance** stored in **MySQL**, **withdraw** it back into inventory items, and **check balance**.

## Commands
- `/vitem balance` — show stored amount
- `/vitem deposit <amount|all>` — move from inventory → virtual
- `/vitem withdraw <amount|all>` — virtual → inventory

Permission: `viv.use` (default: true)

## Build
```
mvn -DskipTests package
```
Drop the shaded jar into `plugins/` and start your server. Configure `config.yml` (MySQL and item settings).

Tested on Paper 1.21.x (Java 21).
