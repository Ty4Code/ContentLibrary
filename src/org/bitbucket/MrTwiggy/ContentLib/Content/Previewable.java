package org.bitbucket.MrTwiggy.ContentLib.Content;

import org.bukkit.entity.Player;

public interface Previewable
{

	/**
	 * Preview the content for the player with given arguments
	 * @param player
	 * @param args
	 */
	public void preview(Player player, String[] args);
	
	/**
	 * Get whether the player can preview the content
	 * @param player
	 * @return true, if the player can preview the content, false otherwise
	 */
	public boolean canPreview(Player player);
}
