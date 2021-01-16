package com.clanjhoo.vampire.accumulator;

import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.util.EntityUtil;
import org.bukkit.entity.Player;

public class UPlayerFoodAccumulator extends UPlayerAccumulator {
	public UPlayerFoodAccumulator(UPlayer uplayer) {
		super(uplayer);
		this.setMin(0);
		this.setMax(20);
	}
	
	@Override
	protected int real() {
		int value = 0;
		Player player = this.uplayer.getPlayer();
		if (player != null)
			value = player.getFoodLevel();
		return value;
	}

	@Override
	protected void real(int val) {
		Player player = this.uplayer.getPlayer();
		if (player != null) {
			player.setFoodLevel(val);
			EntityUtil.sendHealthFoodUpdatePacket(player);
		}
	}
}
