package com.sanhiruzu.amphibia.infrastructure.display;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.Gene;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import com.sanhiruzu.amphibia.register.AmphibiaItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.mutable.MutableObject;

public class FrogGeneticsDisplaySource extends SingleLineDisplaySource {

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity sourceBE = context.getSourceBlockEntity();
        if (sourceBE == null) return EMPTY_LINE.copy();

        TransportedItemStackHandlerBehaviour behaviour =
            BlockEntityBehaviour.get(context.level(), sourceBE.getBlockPos(), TransportedItemStackHandlerBehaviour.TYPE);

        ItemStack stack = ItemStack.EMPTY;

        if (behaviour != null) {
            MutableObject<ItemStack> stackHolder = new MutableObject<>(ItemStack.EMPTY);
            behaviour.handleCenteredProcessingOnAllItems(.5f, tis -> {
                stackHolder.setValue(tis.stack);
                return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
            });
            stack = stackHolder.getValue();
        }

        if (stack.isEmpty() || !stack.is(AmphibiaItems.FROG_BUCKET.get())) {
            return EMPTY_LINE.copy();
        }

        FrogGenome genome = stack.get(AmphibiaDataComponents.FROG_DNA);
        if (genome == null) return Component.literal("No Genome Data");

        var heatTrait = genome.getGene(Gene.HEAT_TOLERANCE);
        var viscosityTrait = genome.getGene(Gene.SLIME_VISCOSITY);
        var growthTrait = genome.getGene(Gene.GROWTH_RATE);

        return Component.literal(String.format("HT:%s%s SV:%s%s GR:%s%s",
                heatTrait.geneA(), heatTrait.geneB(),
                viscosityTrait.geneA(), viscosityTrait.geneB(),
                growthTrait.geneA(), growthTrait.geneB()
        ));
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
