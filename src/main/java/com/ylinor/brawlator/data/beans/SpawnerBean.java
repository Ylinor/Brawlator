package com.ylinor.brawlator.data.beans;

import com.flowpowered.math.vector.Vector3i;

public class SpawnerBean {
    private Vector3i position;
    private MonsterBean monsterBean;
    private int quantity;
    private int spawnRate;
    private int range;

    public Vector3i getPosition() {
        return position;
    }

    public void setPosition(Vector3i position) {
        this.position = position;
    }

    public MonsterBean getMonsterBean() {
        return monsterBean;
    }

    public void setMonsterBean(MonsterBean monsterBean) {
        this.monsterBean = monsterBean;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSpawnRate() {
        return spawnRate;
    }

    public void setSpawnRate(int spawnRate) {
        this.spawnRate = spawnRate;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public SpawnerBean(Vector3i position, MonsterBean monsterBean, int quantity, int spawnRate, int range) {
        this.position = position;
        this.monsterBean = monsterBean;
        this.quantity = quantity;
        this.spawnRate = spawnRate;
        this.range = range;
    }

    @Override
    public String toString() {
        return "SpawnerBean{" +
                "position=" + position +
                ", monsterBean=" + monsterBean +
                ", quantity=" + quantity +
                ", spawnRate=" + spawnRate +
                ", range=" + range +
                '}';
    }

}
