/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.hologram.tracking;

import me.filoghost.holographicdisplays.common.PositionCoordinates;
import org.bukkit.Location;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.Objects;

abstract class PositionBasedLineTracker<T extends Viewer> extends LineTracker<T> {

    private static final int ENTITY_VIEW_RANGE = 64;

    protected PositionCoordinates position;
    private boolean positionChanged;

    @MustBeInvokedByOverriders
    @Override
    protected void detectChanges() {
        PositionCoordinates position = getLine().getPosition();
        if (!Objects.equals(this.position, position)) {
            this.position = position;
            this.positionChanged = true;
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void clearDetectedChanges() {
        this.positionChanged = false;
    }

    @Override
    protected final boolean shouldTrackPlayer(CachedPlayer cachedPlayer) {
        Location playerLocation = cachedPlayer.getCachedLocation();
        if (playerLocation.getWorld() != getLine().getWorldIfLoaded()) {
            return false;
        }

        double diffX = Math.abs(playerLocation.getX() - position.getX());
        double diffZ = Math.abs(playerLocation.getZ() - position.getZ());

        return diffX <= (double) ENTITY_VIEW_RANGE
                && diffZ <= (double) ENTITY_VIEW_RANGE
                && getLine().isVisibleTo(cachedPlayer.getBukkitPlayer());
    }

    @MustBeInvokedByOverriders
    @Override
    protected void sendChangesPackets(Viewers<T> viewers) {
        if (positionChanged) {
            sendPositionChangePackets(viewers);
        }
    }

    protected abstract void sendPositionChangePackets(Viewers<T> viewers);

}
