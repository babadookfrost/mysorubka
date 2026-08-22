package com.example

object UpgradeSystem {

    /** Generate 3 random skill choices from available skills */
    fun generateSkillChoices(gameState: GameState): List<SkillDef> {
        val p = gameState.player
        val available = SkillTree.available(p.skillRanks, p.level)
        if (available.isEmpty()) return emptyList()
        return available.shuffled().take(3)
    }

    /** Called when player selects an upgrade */
    fun applyChoice(gameState: GameState, skillId: String) {
        gameState.applySkillUpgrade(skillId)
        gameState.status = "play"
    }

    /** Check if player should be shown upgrade screen */
    fun shouldShowUpgrade(gameState: GameState): Boolean {
        val p = gameState.player
        return p.skillPoints > 0 && generateSkillChoices(gameState).isNotEmpty()
    }
}
