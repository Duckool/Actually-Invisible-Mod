package duckool.actuallyinvisible;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public class GameRules implements ModInitializer {

    //Gamerule category register
    public static final GameRuleCategory ACTUALLY_INVISIBLE_CATEGORY = GameRuleCategory.register(
            Identifier.fromNamespaceAndPath(ActuallyInvisible.MOD_ID, "actually_invisible"));

    //mob detection Gamerule
    public static final GameRule<Boolean> MOB_CAN_DETECT_BOOLEAN_GAMERULE = GameRuleBuilder
            .forBoolean(false)
            .category(ACTUALLY_INVISIBLE_CATEGORY)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ActuallyInvisible.MOD_ID, "mob_can_detect_invisible"));

    //mob reaction Gamerule
    public static final GameRule<Boolean> MOB_CAN_REACT_BOOLEAN_GAMERULE = GameRuleBuilder
            .forBoolean(true)
            .category(ACTUALLY_INVISIBLE_CATEGORY)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ActuallyInvisible.MOD_ID, "mob_can_react_invisible"));

    //gear visivility Gamerule
    public static final GameRule<Boolean> GEAR_VISIBILITY_BOOLEAN_GAMERULE = GameRuleBuilder
            .forBoolean(false)
            .category(ACTUALLY_INVISIBLE_CATEGORY)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ActuallyInvisible.MOD_ID, "is_gear_visible"));

    //bubble visibility range Gamerule
    public static final GameRule<Integer> BUBBLE_VISIBILITY_RANGE_INT_GAMERULE = GameRuleBuilder
            .forInteger(5)
            .category(ACTUALLY_INVISIBLE_CATEGORY)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ActuallyInvisible.MOD_ID, "bubble_visible_range"));

    @Override
    public void onInitialize() {

    }
}
