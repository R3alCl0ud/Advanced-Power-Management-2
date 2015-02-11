/*******************************************************************************
 * Copyright (c) 2012-2013 Yancarlo Ramsey and CJ Bowman
 * Licensed as open source with restrictions. Please see attached LICENSE.txt.
 ******************************************************************************/
package com.kaijin.AdvPowerMan.items;

import java.util.List;

import com.kaijin.AdvPowerMan.AdvancedPowerManagement;
import com.kaijin.AdvPowerMan.Info;
import com.kaijin.AdvPowerMan.tileentities.TEChargingBench;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBenchTools extends Item
{
	public static final String[] benchToolsNames = new String[] {"toolkit", "LV-kit", "MV-kit", "HV-kit"};
	protected IIcon[] itemIcons;

	public ItemBenchTools(String name)
	{
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.tabMisc);
		GameRegistry.registerItem(this, name);
	}

	/**
	 * Gets an icon based on an item's damage value
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1)
	{
		return itemIcons[MathHelper.clamp_int(par1, 0, 3)];
	}

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
		itemIcons = new IIcon[benchToolsNames.length];
		for (int i = 0; i < itemIcons.length; i++)
		{
			itemIcons[i] = iconRegister.registerIcon(Info.TITLE_PACKED + ":" + benchToolsNames[i]);
		}

		// Until/unless a better way is found, register GUI slot backgrounds here.
		Info.iconSlotChargeable = iconRegister.registerIcon(Info.TITLE_PACKED + ":SlotChargeable");
		Info.iconSlotDrainable = iconRegister.registerIcon(Info.TITLE_PACKED + ":SlotDrainable");
		Info.iconSlotInput = iconRegister.registerIcon(Info.TITLE_PACKED + ":SlotInput");
		Info.iconSlotOutput = iconRegister.registerIcon(Info.TITLE_PACKED + ":SlotOutput");
		Info.iconSlotMachineUpgrade = iconRegister.registerIcon(Info.TITLE_PACKED + ":SlotMachineUpgrade");
		Info.iconSlotLinkCard = iconRegister.registerIcon(Info.TITLE_PACKED + ":SlotLinkCard");
		Info.iconSlotPowerSource = new IIcon[3];
		Info.iconSlotPlayerArmor = new IIcon[4];
		for (int i = 0; i < 3; i++)
			Info.iconSlotPowerSource[i] = iconRegister.registerIcon(Info.TITLE_PACKED + ":SlotPowerSource" + Integer.toString(i));
		for (int i = 0; i < 4; i++)
			Info.iconSlotPlayerArmor[i] = iconRegister.registerIcon(Info.TITLE_PACKED + ":SlotPlayerArmor" + Integer.toString(i));
    }

	@Override
	public boolean isRepairable()
	{
		return false;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack)
	{
		int meta = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, 3);
		return "item.benchTools." + benchToolsNames[meta];
	}

	protected void generateItemStack(ItemStack stack, EntityPlayer player)
	{
		EntityItem entityitem = player.dropPlayerItemWithRandomChoice(stack, false);
		entityitem.delayBeforeCanPickup = 0;
	}
	
	/**
	 * This is called when the item is used, before the block is activated.
	 * @param stack The Item Stack
	 * @param player The Player that used the item
	 * @param world The Current World
	 * @param x Target X Position
	 * @param y Target Y Position
	 * @param z Target Z Position
	 * @param side The side of the target hit
	 * @return Return true to prevent any further processing.
	 */
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) 
	{
		if (AdvancedPowerManagement.proxy.isClient()) return false;

		// Test if the target is a charging bench and the item is a component kit. If so, do the upgrade and return true.
		if (world.getBlock(x, y, z) != AdvancedPowerManagement.blockAdvPwrMan || stack.getItemDamage() < 1 || stack.getItemDamage() > 3 || player == null)
		{
			return false;
		}

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TEChargingBench))
		{
			return false;
		}

		int recoveredTier = ((TEChargingBench)tile).swapBenchComponents(stack.getItemDamage());
		generateItemStack(new ItemStack(AdvancedPowerManagement.itemBenchTools, 1, recoveredTier), player);
		stack.stackSize--;
		return true;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (player.isSneaking() && stack.getItemDamage() > 0 && stack.getItemDamage() < 4)
		{
			switch (stack.getItemDamage())
			{
			case 1:
				generateItemStack(Info.componentCopperCable.copy(), player);
				generateItemStack(Info.componentBatBox.copy(), player);
				break;
			case 2:
				generateItemStack(Info.componentGoldCable.copy(), player);
				generateItemStack(Info.componentMFE.copy(), player);
				break;
			case 3:
				generateItemStack(Info.componentIronCable.copy(), player);
				generateItemStack(Info.componentMFSU.copy(), player);
				break;
			}
			generateItemStack(Info.componentCircuit.copy(), player);
			stack.stackSize--;
		}
		return stack;
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int meta = 0; meta < 4; ++meta)
		{
			par3List.add(new ItemStack(par1, 1, meta));
		}
	}
}
