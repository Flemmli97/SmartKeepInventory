Smart Keep Inventory 1.1.0
================
- Add option to invert logic for player and killer predicate. For example
```json
// Suppose you want to invert this
{
  "killer": {
    "type": "#c:bosses"
  }
}
// Then wrap it like this. This now targets all mobs except bosses
{
  "killer": {
      "invert": true,
      "predicate": {
        "type": "#c:bosses"
      }
  }
}
```

Smart Keep Inventory 1.0.1
================
- Update to 26.1  
~~- Add keep inventory to bosses by default (instead of having a separate datapack)~~

Smart Keep Inventory 1.0.0
================
- Initial release