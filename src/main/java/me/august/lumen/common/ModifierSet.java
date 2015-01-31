package me.august.lumen.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.august.lumen.common.Modifier.*;

public class ModifierSet {

    private int value;

    public ModifierSet(int value) {
        this.value = value;
    }

    public ModifierSet(Modifier... mods) {
        this.value = Modifier.compose(mods);
    }

    public boolean isAbstract() {
        return (value & ABSTRACT.value) != 0;
    }

    public boolean isInterface() {
        return (value & INTERFACE.value) != 0;
    }

    public boolean isFinal() {
        return (value & FINAL.value) != 0;
    }

    public boolean isStatic() {
        return (value & STATIC.value) != 0;
    }

    public boolean isPublic() {
        return (value & PUBLIC.value) != 0;
    }

    public boolean isPrivate() {
        return (value & PRIVATE.value) != 0;
    }

    public boolean isProtected() {
        return (value & PROTECTED.value) != 0;
    }

    public boolean isPackagePrivate() {
        return !(isPublic() || isPrivate() || isProtected());
    }

    public boolean isVolatile() {
        return (value & VOLATILE.value) != 0;
    }

    public boolean isSynchronized() {
        return (value & SYNCHRONIZED.value) != 0;
    }

    public boolean isNative() {
        return (value & NATIVE.value) != 0;
    }

    public void setAbstract(boolean _abstract) {
        if (_abstract) {
            value |= ABSTRACT.value;
        } else {
            value &= ~ABSTRACT.value;
        }
    }

    public void setInterface(boolean _interface) {
        if (_interface) {
            value |= INTERFACE.value;
        } else {
            value &= ~INTERFACE.value;
        }
    }

    public void setFinal(boolean _final) {
        if (_final) {
            value |= FINAL.value;
        } else {
            value &= ~FINAL.value;
        }
    }

    public void setStatic(boolean _static) {
        if (_static) {
            value |= STATIC.value;
        } else {
            value &= ~STATIC.value;
        }
    }

    public void setPublic(boolean _public) {
        if (_public) {
            value |= PUBLIC.value;

            // unset private and protected
            value &= ~PRIVATE.value;
            value &= ~PROTECTED.value;
        } else {
            value &= ~PUBLIC.value;
        }
    }

    public void setPrivate(boolean _private) {
        if (_private) {
            value |= PRIVATE.value;

            // unset public and protected
            value &= ~PUBLIC.value;
            value &= ~PROTECTED.value;
        } else {
            value &= ~PRIVATE.value;
        }
    }

    public void setProtected(boolean _protected) {
        if (_protected) {
            value |= PROTECTED.value;

            // unset public and private
            value &= ~PUBLIC.value;
            value &= ~PRIVATE.value;
        } else {
            value &= ~PROTECTED.value;
        }
    }

    public void setPackagePrivate(boolean pkPriv) {
        if (pkPriv) {
            // unset all other modifiers
            value &= ~PUBLIC.value;
            value &= ~PROTECTED.value;
            value &= ~PRIVATE.value;
        }
    }

    public void setVolatile(boolean _volatile) {
        if (_volatile) {
            value |= VOLATILE.value;
        } else {
            value &= ~VOLATILE.value;
        }
    }

    public void setSynchronized(boolean sync) {
        if (sync) {
            value |= SYNCHRONIZED.value;
        } else {
            value &= ~SYNCHRONIZED.value;
        }
    }

    public void setNative(boolean _native) {
        if (_native) {
            value |= NATIVE.value;
        } else {
            value &= ~NATIVE.value;
        }
    }

    public void add(Modifier modifier) {
        value |= modifier.value;
    }

    public void remove(Modifier modifier) {
        value &= ~modifier.getValue();
    }

    public Modifier[] toArray() {
        return Modifier.fromAccess(value);
    }

    public List<Modifier> toList() {
        return new ArrayList<>(Arrays.asList(toArray()));
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModifierSet that = (ModifierSet) o;

        if (value != that.value) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "ModifierSet" + toList();
    }
}
