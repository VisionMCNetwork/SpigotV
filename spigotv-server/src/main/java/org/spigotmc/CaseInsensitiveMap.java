/*
 * Decompiled with CFR 0.152.
 */
package org.spigotmc;

import gnu.trove.map.hash.TCustomHashMap;
import java.util.Map;
import org.spigotmc.CaseInsensitiveHashingStrategy;

public class CaseInsensitiveMap<V>
extends TCustomHashMap<String, V> {
    public CaseInsensitiveMap() {
        super(CaseInsensitiveHashingStrategy.INSTANCE);
    }

    public CaseInsensitiveMap(Map<? extends String, ? extends V> map) {
        super(CaseInsensitiveHashingStrategy.INSTANCE, map);
    }
}

