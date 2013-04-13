package org.bitbucket.MrTwiggy.ContentLib.Content;

import org.bukkit.entity.Player;

/**
 * An interface implemented by Content to designate that it
 * may be previewed by a player in real time.
 * @author Ty
 *
 */
public interface Previewable
{

	/**
	 * Preview the content for the player with given arguments
	 * @param player - The player previewing the content
	 * @param args - The arguments passed in by the player while attempted to preview
	 */
	public void preview(Player player, String[] args);
	
	/**
	 * Get whether the player can preview the content
	 * @param player - The player attempted to preview the content
	 * @return true, if the player can preview the content, false otherwise
	 */
	public boolean canPreview(Player player);
}
