package info.partonetrain.ocelotfix.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ocelot.class)
public abstract class OcelotMixin {

	@Inject(at = @At("RETURN"), method = "mobInteract")
	private void ocelotfix_spawnCat(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		Ocelot ocelot = (Ocelot)(Object)this;
		if(ocelot.isTrusting())
		{
			Level level = ocelot.level();

			if (!level.isClientSide){
				Cat cat = (Cat)EntityType.CAT.create((ServerLevel) level);

				cat.tame(player); //this sets owner UUID
				cat.setPos(ocelot.position());

				cat.setOrderedToSit(true);
				cat.level().broadcastEntityEvent(cat, (byte)7);
				cat.setPersistenceRequired();

				cat.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(ocelot.getOnPos()), MobSpawnType.NATURAL, (SpawnGroupData)null, (CompoundTag)null);

				cat.moveTo(ocelot.getOnPos().above(), ocelot.getYRot(), ocelot.getXRot()); //this is needed to set rotation for some reason. setXRot doesn't work

				((ServerLevel) level).addFreshEntityWithPassengers(cat);
				ocelot.remove(Entity.RemovalReason.DISCARDED);
			}
		}
	}
}