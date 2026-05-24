# Java to BBModel Converter

A Python converter that transforms Minecraft Java `ModelBase` classes into BlockBench BBModel JSON format.

## Features

- **Parses Java ModelBase syntax** - Extracts ModelRenderer parts, textures, boxes, rotations, and positions
- **Converts to BBModel format** - Generates valid BlockBench 4.5+ compatible JSON
- **Preserves model structure** - Maintains all rotation points, rotations (converted from radians to degrees), and UV mappings
- **Handles texture coordinates** - Extracts texture dimensions and UV positions from the Java code

## Usage

### Command Line

```bash
python converter.py <input.java> <output.bbmodel.json>
```

### Example

```bash
python converter.py internal/model.java internal/cricket.bbmodel.json
```

### In Python Code

```python
from converter import JavaModelParser, BBModelGenerator
from pathlib import Path

# Parse the Java model
parser = JavaModelParser(Path("internal/model.java"))
parser.parse()

# Generate BBModel
generator = BBModelGenerator(parser)
generator.save(Path("output.bbmodel.json"))
```

## What Gets Converted

The converter extracts:

- **Model name** - From the Java class name
- **Texture dimensions** - `textureWidth` and `textureHeight`
- **Model parts** - All `ModelRenderer` fields
- **Geometry** - Box dimensions from `addBox()` calls
- **Positioning** - Rotation points from `setRotationPoint()`
- **Rotations** - Rotation angles from `setRotation()` calls (converted from radians to degrees)
- **Texture UV** - Texture coordinates from the `ModelRenderer` constructor

## Known Limitations

- **Parent-child relationships** - This converter creates a flat outliner. The generated JSON doesn't preserve hierarchical parent-child relationships between parts (they were implicit in the render order of the Java code). You may need to manually organize these in BlockBench.
- **Animation states** - The Java code contains animation logic in `setRotationAngles()` which is not converted. You'll need to recreate animations in BlockBench if needed.
- **Conditional rendering** - Parts that are conditionally rendered (like the wing variants in this model) will all appear in the output. You may want to organize these into groups or separate models.
- **UV scaling** - The converter applies the same UV coordinates to all faces of a box (a simplification). BlockBench may need adjustments for proper per-face UV mapping.

## Generated File Format

The output is a valid BlockBench BBModel v4.5 JSON file that can be:
1. Opened directly in BlockBench
2. Edited and refined visually
3. Exported to other formats (OBJ, Bedrock, etc.)

## Testing

The converter was tested on the Cricket model which contains:
- 20 model parts
- Various box sizes (some with 0 height for planes)
- Multiple rotation angles
- Complex geometry

Result: ✅ Successfully converted to valid BBModel JSON
