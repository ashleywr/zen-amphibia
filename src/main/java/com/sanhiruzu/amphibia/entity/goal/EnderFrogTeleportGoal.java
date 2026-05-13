package com.sanhiruzu.amphibia.entity.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import com.simibubi.create.AllBlocks;

import java.util.EnumSet;

public class EnderFrogTeleportGoal extends Goal {
    private final Frog frog;
    private final double teleportRange;
    private int cooldown = 0;

    public EnderFrogTeleportGoal(Frog frog, double range) {
        this.frog = frog;
        this.teleportRange = range;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.frog.level() != null && !this.frog.level().isClientSide && this.cooldown == 0;
    }

    @Override
    public void tick() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return;
        }

        // 30% chance to teleport each tick when active
        if (this.frog.getRandom().nextDouble() < 0.3) {
            // Try to teleport toward nearby machinery first
            if (this.tryTeleportTowardMachinery()) {
                return;
            }
            // Otherwise, random teleport
            this.randomTeleport();
        }
    }

    private boolean tryTeleportTowardMachinery() {
        BlockPos frogPos = this.frog.blockPosition();
        int searchRadius = 16;

        // Look for Create machinery (kinetic blocks, contraptions, etc)
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos checkPos = frogPos.offset(x, y, z);
                    if (this.isMachinery(checkPos)) {
                        // Found machinery, teleport toward it
                        BlockPos targetPos = checkPos.offset(
                            this.frog.getRandom().nextInt(3) - 1,
                            this.frog.getRandom().nextInt(3) - 1,
                            this.frog.getRandom().nextInt(3) - 1
                        );

                        var blockState = this.frog.level().getBlockState(targetPos);
                        // Teleport to air blocks
                        if (blockState.isAir()) {
                            this.frog.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
                            this.spawnEnderParticles();
                            this.cooldown = 20;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isMachinery(BlockPos pos) {
        var blockState = this.frog.level().getBlockState(pos);
        var block = blockState.getBlock();

        // Check for Create kinetic blocks
        if (blockState.hasProperty(net.minecraft.world.level.block.RotatedPillarBlock.AXIS)) {
            return true;
        }

        // Check for Create-specific blocks
        try {
            String blockName = block.builtInRegistryHolder().key().location().getPath();
            return blockName.contains("gear") || blockName.contains("shaft") || blockName.contains("bearing") ||
                   blockName.contains("cogwheel") || blockName.contains("chain") || blockName.contains("belt");
        } catch (Exception e) {
            return false;
        }
    }

    private void randomTeleport() {
        double x = this.frog.getX() + (this.frog.getRandom().nextDouble() - 0.5) * this.teleportRange;
        double y = this.frog.getY() + (this.frog.getRandom().nextDouble() - 0.5) * 8;
        double z = this.frog.getZ() + (this.frog.getRandom().nextDouble() - 0.5) * this.teleportRange;

        this.frog.teleportTo(x, y, z);
        this.spawnEnderParticles();
        this.cooldown = 20;
    }

    private void spawnEnderParticles() {
        if (this.frog.level() != null && !this.frog.level().isClientSide) {
            for (int i = 0; i < 8; i++) {
                double px = this.frog.getX() + (this.frog.getRandom().nextDouble() - 0.5) * 2;
                double py = this.frog.getY() + this.frog.getRandom().nextDouble() * 2;
                double pz = this.frog.getZ() + (this.frog.getRandom().nextDouble() - 0.5) * 2;

                this.frog.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.PORTAL,
                    px, py, pz,
                    this.frog.getRandom().nextGaussian() * 0.1,
                    this.frog.getRandom().nextGaussian() * 0.1,
                    this.frog.getRandom().nextGaussian() * 0.1
                );
            }
        }
    }

    @Override
    public void stop() {
        this.cooldown = 0;
    }
}
