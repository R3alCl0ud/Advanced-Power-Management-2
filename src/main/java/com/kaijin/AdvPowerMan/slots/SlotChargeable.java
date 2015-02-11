/*******************************************************************************
 * Copyright (c) 2012-2013 Yancarlo Ramsey and CJ Bowman
 * Licensed as open source with restrictions. Please see attached LICENSE.txt.
 ******************************************************************************/
package com.kaijin.AdvPowerMan.slots;

import com.kaijin.AdvPowerMan.Info;
import com.kaijin.AdvPowerMan.Utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class SlotChargeable extends SlotCustom
{
	public int chargeTier;
	
	public SlotChargeable(IInventory inv, int index, int xpos, int ypos, int tier)
	{
		super(inv, index, xpos, ypos);
		chargeTier = tier;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		// Decide if the item is a valid IC2 electrical item
		return Utils.isItemChargeable(stack, chargeTier);
	}

	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}

	@Override
	public IIcon getBackgroundIconIndex()
	{
		return Info.iconSlotChargeable;
		//return 247;
	}
}

