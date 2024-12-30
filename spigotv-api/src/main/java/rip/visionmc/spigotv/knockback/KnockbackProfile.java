package rip.visionmc.spigotv.knockback;

public interface KnockbackProfile {

    String getName();

    String[] getValues();

    double getHorizontal();

    void setHorizontal(double horizontal);

    double getVertical();

    void setVertical(double vertical);

    double getMinRange();

    void setMinRange(double minRange);

    double getMaxRange();

    void setMaxRange(double maxRange);

    double getStartRange();

    void setStartRange(double startRange);

    double getRangeFactor();

    void setRangeFactor(double rangeFactor);

    double getHorizontalFriction();

    void setHorizontalFriction(double horizontalFriction);

    double getVerticalFriction();

    void setVerticalFriction(double verticalFriction);

    void save();
}
