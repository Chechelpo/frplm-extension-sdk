package io.github.chechelpo.frplm.extensions.api.standalone;

import io.github.chechelpo.frplm.extensions.api.utils.EntityConfigs;
import io.github.chechelpo.frplm.extensions.api.utils.ReferenceCodec;

public non-sealed interface TagSnapshot extends Snapshot<TagSnapshot.Reference>{
    record Reference(int tagId) implements StableReference{
        private static String PREFIX = EntityConfigs.Types.TAGS.getEntityType();

        @Override
        public String encode() {
            return ReferenceCodec.encode(PREFIX, this.tagId);
        }

        public static Reference fromString(String value){
            return ReferenceCodec.parseOne(value, PREFIX, Reference::new);
        }
    }

    /** @return this tag's name */
    String getName();
}
