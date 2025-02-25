package vazkii.quark.content.tweaks.module;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class RenewableSporeBlossoms extends QuarkModule {
	
	@Config public double boneMealChance = 0.2;
	
	@SubscribeEvent
	public void onBoneMealed(BonemealEvent event) {
		if(event.getBlock().is(Blocks.SPORE_BLOSSOM) && boneMealChance > 0) {
			if(Math.random() < boneMealChance)
				Block.popResource(event.getLevel(), event.getPos(), new ItemStack(Items.SPORE_BLOSSOM));
			
			event.setResult(Result.ALLOW);
		}
	}	

}
