package com.thevoxelbox.voxelborder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.java.JavaPlugin;

public class VoxelBorder extends JavaPlugin
{

    private static String[] commandCompletions = { "create", "remove", "edit", "activezones" };

    private ZoneManager zoneManager;

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args)
    {
        final String commandName = command.getName().toLowerCase();

        if (sender instanceof Player)
        {
            final Player player = (Player) sender;

            if ((commandName.equalsIgnoreCase("voxelborder"))
                    && (player.isOp() ? true : player.hasPermission("voxelborder.editzones")))
            {
                if ((args != null) && (args.length > 0))
                {
                    if (args[0].equalsIgnoreCase("create"))
                    {
                        if (args.length == 6)
                        {
                            final int x1, z1, x2, z2;
                            try
                            {
                                x1 = Integer.parseInt(args[2]);
                                z1 = Integer.parseInt(args[3]);
                                x2 = Integer.parseInt(args[4]);
                                z2 = Integer.parseInt(args[5]);
                            }
                            catch (final Exception e)
                            {
                                sender.sendMessage(ChatColor.GREEN + "Incorrect parameters " + ChatColor.GRAY + "/vBorder <create:remove:edit> [name] x z x z");
                                return true;
                            }
                            Zone newZone = new Zone(args[1], x1, z1, x2, z2, player.getWorld().getUID());
                            this.zoneManager.addZone(newZone);
                            player.sendMessage(ChatColor.GRAY + "Zone successfully created!");
                            this.getLogger().info("Player created border " + newZone);
                        }
                        else
                        {
                            player.sendMessage(ChatColor.GREEN + "Incorrect parameters " + ChatColor.GRAY + "/vBorder <create:remove:edit> [name] x z x z");
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("remove"))
                    {
                        if (args.length == 2)
                        {
                            final Zone oldZone = this.zoneManager.getZone(args[1]);
                            if (oldZone != null)
                            {
                                this.zoneManager.removeZone(oldZone);
                                player.sendMessage(ChatColor.GRAY + "Zone sucessfully removed");
                                this.getLogger().info("Player removed zone " + oldZone);
                                return true;
                            }
                            else
                            {
                                player.sendMessage(ChatColor.RED + "No zone found by name: " + args[1]);
                            }
                        }
                        else
                        {
                            player.sendMessage(ChatColor.GREEN + "Incorrect parameters " + ChatColor.GRAY + "/vBorder <create:remove:edit> [name] x z x z");
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("edit"))
                    {
                        if (args.length == 6)
                        {
                            final Zone oldZone = this.zoneManager.getZone(args[1]);
                            if (oldZone != null)
                            {
                                final String zoneName = oldZone.getName();
                                final UUID worldID = oldZone.getWorldID();
                                final int x1, z1, x2, z2;
                                try
                                {
                                    x1 = Integer.parseInt(args[2]);
                                    z1 = Integer.parseInt(args[3]);
                                    x2 = Integer.parseInt(args[4]);
                                    z2 = Integer.parseInt(args[5]);
                                }
                                catch (final Exception e)
                                {
                                    sender.sendMessage(ChatColor.GREEN + "Incorrect parameters " + ChatColor.GRAY + "/vBorder <create:remove:edit> [name] x z x z");
                                    return true;
                                }
                                this.zoneManager.removeZone(oldZone);
                                this.zoneManager.addZone(new Zone(zoneName, x1, z1, x2, z2, worldID));
                                player.sendMessage(ChatColor.GRAY + "Zone sucessfully edited");
                                this.getLogger().info("Player edited zone " + oldZone.getName() + ", it is now " + this.zoneManager.getZone(oldZone.getName()));
                            }
                            else
                            {
                                player.sendMessage(ChatColor.RED + "No zone named \"" + args[1] + "\" exists!");
                                return true;
                            }
                        }
                    }
                    if (args[0].equalsIgnoreCase("activezones"))
                    	{
                    	    for(String zone : this.zoneManager.getZones()) 
                    	    {
                    	        player.sendMessage(zone);
                    	    }
                    	}
                }
                else
                {
                    sender.sendMessage(ChatColor.GREEN + "Incorrect parameters " + ChatColor.GRAY + "/vBorder <create:remove:edit> [name] x z x z");
                    return true;
                }
            }
            if ((commandName.equalsIgnoreCase("btp")) && (player.isOp() ? true : player.hasPermission("voxelborder.btp")))
            {
                if ((args != null) && (args.length > 0))
                {

                    final List<Player> matches = Bukkit.matchPlayer(args[0]);
                    if (matches.size() > 1)
                    {
                        player.sendMessage(ChatColor.RED + "Partial match");
                    }
                    else if (matches.isEmpty())
                    {
                        player.sendMessage(ChatColor.RED + "No player to match");
                    }
                    else
                    {
                        final Player pl = matches.get(0);
                        final Location loc = pl.getLocation();

                        player.sendMessage(ChatColor.AQUA + "Woosh!");

                        if (args.length < 2)
                        {
                            player.teleport(loc, TeleportCause.ENDER_PEARL);
                        }
                        else
                        {
                            if (args[1].matches("me"))
                            {
                                pl.sendMessage(ChatColor.AQUA + "Woosh!");
                                pl.teleport(player.getLocation(), TeleportCause.ENDER_PEARL);
                                return true;
                            }

                            for (int i = 1; i < args.length; i++)
                            {
                                try
                                {
                                    if (args[i].startsWith("x"))
                                    {
                                        loc.setX(loc.getX() + Double.parseDouble(args[i].replace("x", "")));
                                        continue;
                                    }
                                    else if (args[i].startsWith("y"))
                                    {
                                        loc.setY(loc.getY() + Double.parseDouble(args[i].replace("y", "")));
                                        continue;
                                    }
                                    else if (args[i].startsWith("z"))
                                    {
                                        loc.setZ(loc.getZ() + Double.parseDouble(args[i].replace("z", "")));
                                        continue;
                                    }
                                }
                                catch (final NumberFormatException e)
                                {
                                    player.sendMessage(ChatColor.RED + "Error parsing argument \"" + args[i] + "\"");
                                    return true;
                                }
                            }

                            player.teleport(loc, TeleportCause.ENDER_PEARL);
                        }
                    }
                    return true;
                }
                else
                {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Please specify the target player");
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public void onDisable()
    {
        this.zoneManager.saveZones(ZoneManager.getZoneFile());
    }

    @Override
    public void onEnable()
    {
        this.zoneManager = new ZoneManager(this);
        Bukkit.getPluginManager().registerEvents(new BorderListener(this.zoneManager), this);
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        final List<String> tabCompletions = new ArrayList<String>();
        final String commandName = command.getName().toLowerCase();
        if (commandName.equalsIgnoreCase("voxelborder"))
        {
            if (args.length > 0)
            {
                if (args.length == 1)
                {
                    for (final String subcommand : VoxelBorder.commandCompletions)
                    {
                        if (subcommand.toLowerCase().startsWith(args[0]))
                        {
                            tabCompletions.add(subcommand);
                        }
                    }
                }
                if (args.length == 2)
                {
                    if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit"))
                    {
                        tabCompletions.addAll(this.zoneManager.lookupZone(args[1].toLowerCase()));
                    }
                }
            }
        }
        if (tabCompletions.isEmpty())
        {
            return null;
        }
        Collections.sort(tabCompletions);
        return tabCompletions;
    }
}
