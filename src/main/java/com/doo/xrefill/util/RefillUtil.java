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
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 补充工具
 */
public class RefillUtil {

    /**
     * 玩家缓存
     */
    private static final Map<PlayerEntity, ServerPlayerEntity> PLAYER_MAP = new HashMap<>(1);

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
     * @param hash 物品栈hash
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
        MinecraftServer server = serverPlayer.getServer();
        if (server == null) {
            return;
        }
        Arrays.stream(EquipmentSlot.values()).filter(e -> serverPlayer.getEquippedStack(e).hashCode() == hash)
                .findAny().ifPresent(e -> {
                    // 找出背包中相同物品
                    DefaultedList<ItemStack> stacks = serverPlayer.inventory.main;
                    ItemStack temp;
                    for (int i = 0; i < stacks.size(); i++) {
                        temp = stacks.get(i);
                        if (!temp.isEmpty() && isSameItem(item, temp.getItem())) {
                            // 异步补充
                            int idx =  i;
                            ItemStack refill = temp.copy();
                            server.send(new ServerTask(serverPlayer.age + 100, () -> {
                                if (serverPlayer.getEquippedStack(e).isEmpty()) {
                                    serverPlayer.equipStack(e, refill);
                                    serverPlayer.equip(idx, ItemStack.EMPTY);
                                }
                            }));
                            return;
                        }
                    }
                });
    }

    /**
     * 是相同的物品
     *
     * @param item 物品1
     * @param item2 物品2
     * @return true or false
     */
    private static boolean isSameItem(Item item, Item item2) {
        // 同样的物品 same
        return item == item2
                // 同样的物品 class same
                || item.getClass() == item2.getClass()
                // 都是食物 food
                || (item.isFood() && item2.isFood())
                // 都是建筑块 building block
                || (item.getGroup() == ItemGroup.BUILDING_BLOCKS && item2.getGroup() == ItemGroup.BUILDING_BLOCKS)
                ;
    }
}
