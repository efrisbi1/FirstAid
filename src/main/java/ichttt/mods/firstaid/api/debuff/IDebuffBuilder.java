/*
 * FirstAid API
 * Copyright (c) 2017-2019
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package ichttt.mods.firstaid.api.debuff;

import ichttt.mods.firstaid.api.enums.EnumPlayerDebuffSlot;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Use this if you want to add simple onHit or constant debuffs.
 * <br>
 * If you want to do your own, custom implementation, you can use {@link ichttt.mods.firstaid.api.debuff.IDebuff}
 * directly and register it using {@link ichttt.mods.firstaid.api.FirstAidRegistry#registerDebuff(EnumPlayerDebuffSlot, EntityEquipmentSlot, Supplier)}.
 */
public interface IDebuffBuilder {

    /**
     * Adds a sound to the debuff. The sound will be played as long as the debuff is timed.
     * <b>Does only work with onHit debuffs!</b>
     *
     * @param event The sound that should be player
     * @return this
     */
    @Nonnull
    IDebuffBuilder addSoundEffect(@Nullable SoundEvent event);

    /**
     * If OnHit damage: value = absolute damage taken for this multiplier to apply;
     * If Constant: value = percentage of health left for this multiplier
     *
     * @param value      absolute damage (onHit) or percentage of the health left (constant)
     * @param multiplier the potion effect multiplier
     * @return this
     */
    @Nonnull
    IDebuffBuilder addBound(float value, int multiplier);

    /**
     * Provide a boolean supplier to control runtime-disabling of certain effects (e.g. via config)
     *
     * @param isEnabled A supplier that should return true if the debug should be applied
     * @return this
     */
    @Nonnull
    IDebuffBuilder addEnableCondition(@Nullable BooleanSupplier isEnabled);

    /**
     * Builds and registers this debuff to the FirstAid registry.
     * This is the final step.
     * This does the same as {@link ichttt.mods.firstaid.api.FirstAidRegistry#registerDebuff(EnumPlayerDebuffSlot, EntityEquipmentSlot, IDebuffBuilder)}
     *
     * @param playerSlot The slot where the debuff should apply if the entity is a player
     * @param generalSlot The slot where the debuff should apply on any other entity. Use null if this is player-specific
     */
    void register(@Nonnull EnumPlayerDebuffSlot playerSlot, @Nullable EntityEquipmentSlot generalSlot);
}
