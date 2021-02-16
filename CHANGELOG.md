
# v4.1.0
- Added additional overloads of `AbstractTextStyle.getAttribute()` to reduce boxing conversions between e.g. `float`/`Float`.
- Garbage collection of native resources is more resilient against exceptions.

# v4.0.1
- `GdxFont` now disposes its native resources automatically when garbage collected.

# v4.0.0
- Changed `GdxFontStore` (which manages lifetimes of included fonts) to `GdxFontRegistry` (which doesn't). This allows more flexibility in memory management for users of this library.
