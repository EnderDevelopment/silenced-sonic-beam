# Minecraft Plugin: Silenced Sonic Beam

Silenced Sonic Beam is a Minecraft plugin that gives players a Warden sonic beam ability when they wear at least one armor piece with the Silence armor trim. The Minecraft plugin uses a sneaking right-click activation, a 20-block ray-traced beam, Warden sound and sonic boom particles, 8 points of health removal, and a five-second cooldown.

## Install

1. Build the plugin with Java 21 and Maven:
   ```
   mvn package
   ```
2. Copy the generated `target/plugin.jar` file into the server's `plugins` folder.
3. Start or restart a Paper Minecraft server running version 1.21 or newer.
4. Confirm that the plugin is enabled in the server console.

## Use

1. Equip at least one armor piece with the Silence armor trim.
2. Face the target within 20 blocks.
3. Sneak and right-click the air or a block.
4. The beam follows the player's line of sight and stops at the first block or living entity it reaches.
5. A living entity hit by the beam loses 8 health points, equal to 4 hearts.
6. The ability has a five-second cooldown. An action-bar message displays the remaining recharge time when it is unavailable.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/48fa3989-76b5-435e-b209-9d58f617b23d?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=48fa3989-76b5-435e-b209-9d58f617b23d
