package com.github.krgermax.data.inventory;

public class UserStats {
    private String userID;
    private int minedCount;
    private double xpCount;
    private int goldCount;
    private int mobKills;
    private int bossKills;
    private int rank = -1;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getMinedCount() {
        return minedCount;
    }

    public void setMinedCount(int minedCount) {
        this.minedCount = minedCount;
    }

    public double getXpCount() {
        return xpCount;
    }

    public void setXpCount(double xpCount) {
        this.xpCount = xpCount;
    }

    public int getGoldCount() {
        return goldCount;
    }

    public void setGoldCount(int goldCount) {
        this.goldCount = goldCount;
    }

    public int getMobKills() {
        return mobKills;
    }

    public void setMobKills(int mobKills) {
        this.mobKills = mobKills;
    }

    public int getBossKills() {
        return bossKills;
    }

    public void setBossKills(int bossKills) {
        this.bossKills = bossKills;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
