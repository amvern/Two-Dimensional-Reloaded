package github.amvern.twodimensionalreloaded.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class RandomHelpers {
    public static boolean isLookingAtEndermanHead(Player player, EnderMan enderman) {
        HitResult hit = ((LocalPlayer)player).raycastHitResult(0.0f, Minecraft.getInstance().getCameraEntity());
//        HitResult hit = player.ray(20.0D, 0.0F, false);

        if (hit.getType() != HitResult.Type.ENTITY) return false;

        Entity entity = ((EntityHitResult) hit).getEntity();
        if (entity != enderman) return false;

        double hitY = hit.getLocation().y;

        double headMin = enderman.getY() + enderman.getEyeHeight() * 0.85;
        double headMax = enderman.getY() + enderman.getEyeHeight() + 0.3;

        return hitY >= headMin && hitY <= headMax;
    }
}