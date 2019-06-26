package io.github.rednesto.bou.common.requirement;

import com.google.common.base.MoreObjects;

public abstract class AbstractRequirement<T> implements Requirement<T> {

    private final String id;
    private final Class<T> applicableType;

    protected AbstractRequirement(String id, Class<T> applicableType) {
        this.id = id;
        this.applicableType = applicableType;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Class<T> getApplicableType() {
        return this.applicableType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractRequirement)) {
            return false;
        }

        AbstractRequirement<?> that = (AbstractRequirement<?>) o;
        return id.equals(that.id) &&
                applicableType.equals(that.applicableType);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("applicableType", applicableType)
                .toString();
    }
}
