package com.sanhiruzu.amphibia.client;

import com.sanhiruzu.amphibia.block.DormantFrogspawnBlockEntity;
import com.sanhiruzu.amphibia.block.GeneticFrogspawnBlockEntity;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.FrogState;
import com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class CrosshairTooltipHandler {

    public static void renderIfNeeded(GuiGraphics guiGraphics, Minecraft mc) {
        if (mc.options.hideGui || mc.gameMode == null || mc.player == null || mc.screen != null) return;

        boolean overlayEnabled = mc.player.getData(AmphibiaAttachments.FROG_DNA_OVERLAY_ENABLED);
        boolean wearingGoggles = false;
        if (ModList.get().isLoaded("create")) {
            wearingGoggles = com.simibubi.create.content.equipment.goggles.GogglesItem.isWearingGoggles(mc.player);
        }
        if (!overlayEnabled && !wearingGoggles) return;

        List<Component> tooltip = buildTooltip(mc, mc.hitResult);
        if (tooltip.isEmpty()) return;

        render(guiGraphics, mc, tooltip);
    }

    private static List<Component> buildTooltip(Minecraft mc, HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHit) {
            var entity = entityHit.getEntity();
            if (entity instanceof Tadpole tadpole) return forTadpole(tadpole);
            if (entity instanceof Frog frog) return forFrog(frog);
        }

        if (hitResult instanceof BlockHitResult blockHit && mc.level != null) {
            var state = mc.level.getBlockState(blockHit.getBlockPos());
            if (state.is(Blocks.FROGSPAWN)) return forVanillaFrogspawn();

            var be = mc.level.getBlockEntity(blockHit.getBlockPos());
            if (be instanceof GeneticFrogspawnBlockEntity frogspawn)
                return forGeneticFrogspawn(mc, frogspawn);
            if (be instanceof DormantFrogspawnBlockEntity frogspawn)
                return forDormantFrogspawn(mc, frogspawn);
        }

        return List.of();
    }

    private static List<Component> forVanillaFrogspawn() {
        return List.of(
            Component.translatable("block.minecraft.frogspawn").withStyle(ChatFormatting.YELLOW),
            Component.translatable("tooltip.zen_amphibia.vanilla_frogspawn.description").withStyle(ChatFormatting.GRAY),
            Component.translatable("tooltip.zen_amphibia.vanilla_frogspawn.action").withStyle(ChatFormatting.DARK_AQUA)
        );
    }

    private static List<Component> forGeneticFrogspawn(Minecraft mc, GeneticFrogspawnBlockEntity frogspawn) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("block.zen_amphibia.genetic_frogspawn").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.zen_amphibia.genetic_frogspawn.description").withStyle(ChatFormatting.GRAY));
        long age = mc.level.getGameTime() - frogspawn.getSpawnedAt();
        int rts = mc.level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        tooltip.add(Component.literal("Age: " + (age / 20) + "s  |  randomTickSpeed: " + rts).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.addAll(FrogDNADisplayHelper.getDNATooltip(frogspawn.getGenome(), true));
        return tooltip;
    }

    private static List<Component> forDormantFrogspawn(Minecraft mc, DormantFrogspawnBlockEntity frogspawn) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("block.zen_amphibia.dormant_frogspawn").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.zen_amphibia.dormant_frogspawn.description").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.zen_amphibia.dormant_frogspawn.action").withStyle(ChatFormatting.DARK_AQUA));
        long age = mc.level.getGameTime() - frogspawn.getSpawnedAt();
        tooltip.add(Component.literal("Age: " + (age / 20) + "s").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.addAll(FrogDNADisplayHelper.getDNATooltip(frogspawn.getGenome(), true));
        return tooltip;
    }

    private static List<Component> forFrog(Frog frog) {
        FrogState state = FrogState.fromFrog(frog);
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("=== FROG INFO ===").withStyle(ChatFormatting.LIGHT_PURPLE));
        lines.add(Component.literal("Age: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(state.age + " ticks").withStyle(ChatFormatting.YELLOW)));
        lines.add(Component.literal("Size: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(String.format("%.2f", state.scale)).withStyle(ChatFormatting.YELLOW)));
        lines.addAll(FrogDNADisplayHelper.getComprehensiveDNATooltip(state.genome));
        lines.add(Component.empty());
        lines.add(Component.literal("Status: ").withStyle(ChatFormatting.GRAY)
            .append(modStatus(state)));
        lines.add(Component.literal("AI: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(state.aiStatus).withStyle(ChatFormatting.DARK_GRAY)));
        return lines;
    }

    private static Component modStatus(FrogState state) {
        if (state.maturityProgress < 1.0f) {
            int pct = (int)(state.maturityProgress * 100);
            return Component.literal("Maturing " + pct + "%").withStyle(ChatFormatting.YELLOW);
        }
        if (state.estivating)
            return Component.literal("Estivating").withStyle(ChatFormatting.AQUA);
        if (state.hasEgg && state.inWater)
            return Component.literal("Laying eggs").withStyle(ChatFormatting.GREEN);
        if (state.hasEgg)
            return Component.literal("Finding water to lay eggs").withStyle(ChatFormatting.YELLOW);
        if (state.inLove)
            return Component.literal("In love").withStyle(ChatFormatting.LIGHT_PURPLE);
        return Component.literal("Idle").withStyle(ChatFormatting.WHITE);
    }

    private static List<Component> forTadpole(Tadpole tadpole) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("=== TADPOLE ===").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("Age: " + (tadpole.tickCount / 20) + "s").withStyle(ChatFormatting.GRAY));

        boolean stunted = tadpole.getData(AmphibiaAttachments.STUNTED_GROWTH);
        boolean accelerated = tadpole.getData(AmphibiaAttachments.ACCELERATED_GROWTH);
        if (stunted) {
            tooltip.add(Component.literal("Growth: STUNTED").withStyle(ChatFormatting.RED));
        } else if (accelerated) {
            tooltip.add(Component.literal("Growth: ACCELERATED").withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.literal("Growth: normal").withStyle(ChatFormatting.WHITE));
        }

        FrogGenome genome = tadpole.getData(AmphibiaAttachments.FROG_GENOME);
        if (genome != null) {
            tooltip.addAll(FrogDNADisplayHelper.getDNATooltip(genome, false));
        }
        return tooltip;
    }

    // Height of the hotbar HUD area (slots + selection indicator) in GUI units.
    private static final int HOTBAR_MARGIN = 26;

    private static void render(GuiGraphics guiGraphics, Minecraft mc, List<Component> tooltip) {
        var lines = tooltip.stream().map(Component::getVisualOrderText).toList();

        int maxWidth = 0;
        for (var line : lines) {
            int w = mc.font.width(line);
            if (w > maxWidth) maxWidth = w;
        }
        int lineHeight = mc.font.lineHeight + 1;
        int tooltipW = maxWidth + 12;
        int tooltipH = lines.size() * lineHeight + 6;

        int cx = guiGraphics.guiWidth() / 2;
        int cy = guiGraphics.guiHeight() / 2;
        int x = cx + 15;
        int y = cy + 15;

        // Keep the tooltip away from the right edge and above the hotbar.
        if (x + tooltipW > guiGraphics.guiWidth() - 4) x = cx - tooltipW - 17;
        if (y + tooltipH > guiGraphics.guiHeight() - HOTBAR_MARGIN) y = guiGraphics.guiHeight() - tooltipH - HOTBAR_MARGIN;
        if (x < 4) x = 4;
        if (y < 4) y = 4;

        guiGraphics.renderTooltip(mc.font, lines, x, y);
    }
}
