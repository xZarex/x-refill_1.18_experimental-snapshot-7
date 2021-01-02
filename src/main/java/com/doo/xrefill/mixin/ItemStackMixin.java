package com.doo.xrefill.mixin;

import com.doo.xrefill.util.RefillUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract boolean isEmpty();

    @Shadow @Final @Deprecated private Item item;

    private boolean using = false;


    @Inject(method = "use", at = @At("HEAD"))
    private void useH(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> re) {
        if (user instanceof  ServerPlayerEntity) {
            using = true;
        }
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    private void useOnBlockH(ItemUsageContext context, CallbackInfoReturnable<ActionResult> re) {
        if (context.getPlayer() instanceof  ServerPlayerEntity) {
            using = true;
        }
    }

    @Inject(method = "useOnEntity", at = @At("HEAD"))
    private void useOnEntityH(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> re) {
        if (user instanceof  ServerPlayerEntity) {
            using = true;
        }
    }

    @Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"))
    private void damageH(int amount, LivingEntity entity, Consumer<LivingEntity> breakCallback, CallbackInfo info) {
        if (entity instanceof  ServerPlayerEntity) {
            using = true;
        }
    }

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void finishUsingH(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> returnable) {
        if (user instanceof  ServerPlayerEntity) {
            using = true;
        }
    }

    @Inject(method = "setCount", at = @At("TAIL"))
    private void setCountT(int amount, CallbackInfo info) {
        ClientPlayerEntity player;
        if (isEmpty() && using && RefillUtil.exist((player = MinecraftClient.getInstance().player))) {
            RefillUtil.refill(player, hashCode(), item);
        }
        using = false;
    }
}
