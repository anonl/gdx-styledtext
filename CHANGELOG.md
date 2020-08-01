
# v4.0.0
- Changed `GdxFontStore` (which manages lifetimes of included fonts) to `GdxFontRegistry` (which doesn't). This allows more flexibility in memory management for users of this library.
