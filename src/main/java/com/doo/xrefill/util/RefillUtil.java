package com.doo.xrefill.util;

import com.doo.xrefill.Refill;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * 补充工具
 */
public class RefillUtil {

    /**
     * 玩家缓存
     */
    private static final Map<PlayerEntity, ServerPlayerEntity> PLAYER_MAP = new HashMap<>(1);

    /**
     * 不是同一种物品
     */
    private static final int DIFF = 20;

    /**
     * 获取服务端当前玩家
     *
     * @param clientPlayer 本地玩家
     * @return 玩家
     */
    public static boolean exist(ClientPlayerEntity clientPlayer) {
        if (clientPlayer == null) {
            return false;
        }
        ServerPlayerEntity player = PLAYER_MAP.get(clientPlayer);
        if (player == null) {
            PLAYER_MAP.clear();
            MinecraftServer server;
            if ((server = MinecraftClient.getInstance().getServer()) == null) {
                return false;
            }
            PLAYER_MAP.put(clientPlayer, server.getPlayerManager().getPlayer(clientPlayer.getUuid()));
        }
        return true;
    }

    /**
     * 补充
     *
     * @param player 本地玩家
     * @param hash 物品hash
     * @param item 物品
     */
    public static void refill(PlayerEntity player, int hash, Item item) {
        if (!Refill.option.enable) {
            return;
        }
        PlayerEntity serverPlayer = PLAYER_MAP.get(player);
        if (serverPlayer == null) {
            return;
        }
        Arrays.stream(EquipmentSlot.values()).filter(e -> serverPlayer.getEquippedStack(e).hashCode() == hash)
                .findAny().ifPresent(e ->
                    // 找出背包中相同物品
                    serverPlayer.inventory.main.stream()
                            .filter(i -> !i.isEmpty() && getSortNum(i, item) < DIFF)
                            .min(Comparator.comparing(i -> getSortNum(i, item)))
                            .ifPresent(i -> {
                                // 替换
                                ForkJoinPool.commonPool().submit(() -> {
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(200);
                                        if (serverPlayer.getEquippedStack(e).isEmpty()) {
                                            serverPlayer.equipStack(e, i.copy());
                                            i.setCount(0);
                                        }
                                    } catch (Exception ignore) {}
                                });
                            })
                );
    }

    /**
     * 是相同的物品
     *
     * @param itemStack 物品1
     * @param item2 物品2
     * @return true or false
     */
    private static double getSortNum(ItemStack itemStack, Item item2) {
        int sortNum = DIFF;
        Item item = itemStack.getItem();
        if (item == item2) {
            sortNum = 1;
        } else if (item.getClass() == item2.getClass()) {
            sortNum = 2;
        } else if ((item.isFood() && item2.isFood())) {
            sortNum = 3;
        } else if ((item.getGroup() == ItemGroup.BUILDING_BLOCKS && item2.getGroup() == ItemGroup.BUILDING_BLOCKS)) {
            sortNum = 4;
        }
        return sortNum + itemStack.getMaxDamage() / 100000D;
    }
}
