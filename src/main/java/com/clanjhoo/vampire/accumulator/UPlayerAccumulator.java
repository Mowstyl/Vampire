package com.clanjhoo.vampire.accumulator;

import com.clanjhoo.vampire.entity.UPlayer;

public abstract class UPlayerAccumulator extends Accumulator {
	protected UPlayer uplayer;

	public UPlayerAccumulator(UPlayer uplayer) {
		this.uplayer = uplayer;
	}
}
