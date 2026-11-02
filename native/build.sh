#!/bin/sh
# Build the native hook libraries for QNX 6.5 armle-v7 on any host with clang and ld.lld
# (macOS: brew install llvm lld). No QNX SDK and no files from the unit are needed: the
# unit's libc.so.3 and libsocket.so.3 are replaced at link time by stubs that only carry the
# symbol names. Each directory src/<name>/ becomes libmcn_<name>.so, with src/common/ linked into
# every one; its entry point is
# mcn_init, called through DT_INIT: the QNX 6.5 run-time linker predates .init_array, so no
# constructors are used.
set -e
HERE="$(cd "$(dirname "$0")" && pwd)"
if [ -n "${ZIG:-}" ]; then
    CLANG="$ZIG cc"
    LLD="$ZIG ld.lld"
    TARGET="-target arm-freestanding-eabi -mcpu=cortex_a8"
else
    TARGET="--target=armv7a-none-eabi"
fi
if [ -z "${CLANG:-}" ]; then
    if [ -n "${LLVM:-}" ]; then
        CLANG="$LLVM/clang"
    elif command -v clang >/dev/null 2>&1; then
        CLANG="$(command -v clang)"
    elif command -v brew >/dev/null 2>&1; then
        CLANG="$(brew --prefix llvm)/bin/clang"
    else
        echo "clang was not found; set CLANG or LLVM." >&2
        exit 1
    fi
fi
if [ -z "${LLD:-}" ]; then
    if command -v ld.lld >/dev/null 2>&1; then
        LLD="$(command -v ld.lld)"
    elif command -v brew >/dev/null 2>&1; then
        LLD="$(brew --prefix lld)/bin/ld.lld"
    else
        echo "ld.lld was not found; set LLD." >&2
        exit 1
    fi
fi
OUT="$HERE/build"
CFLAGS="$TARGET -marm -mfloat-abi=soft -fPIC -Os -ffreestanding -nostdlib
 -fno-stack-protector -fno-builtin -fvisibility=hidden -std=c99 -Wall -Wextra -Werror
 -I$HERE/include"
LDFLAGS="-shared -z defs -z max-page-size=0x1000 --hash-style=sysv --gc-sections"

rm -rf "$OUT"
mkdir -p "$OUT/stubs"
for stub in "$HERE"/stubs/*_stub.c; do
    lib=$(basename "$stub" _stub.c)
    $CLANG $CFLAGS -fvisibility=default -c "$stub" -o "$OUT/stubs/$lib.o"
    $LLD -shared --hash-style=sysv -soname "$lib.so.3" -o "$OUT/stubs/$lib.so.3" "$OUT/stubs/$lib.o"
done

for name in sal ipod; do
    dir="$HERE/src/$name/"
    objs=""
    if [ "$name" = sal ]; then
        sources="$dir/sal.c $dir/iap2.c"
    else
        sources="$dir/ipod.c"
    fi
    for src in $sources "$HERE"/src/common/*.c; do
        obj="$OUT/$name-$(basename "$(dirname "$src")")-$(basename "$src" .c).o"
        $CLANG $CFLAGS -c "$src" -o "$obj"
        objs="$objs $obj"
    done
    $LLD $LDFLAGS -soname "libmcn_$name.so" -init mcn_init -o "$OUT/libmcn_$name.so" $objs \
        "$OUT"/stubs/*.so.3
    echo "$OUT/libmcn_$name.so"
done
