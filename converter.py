#!/usr/bin/env python3
"""
Converter from Minecraft Java ModelBase to BBModel JSON format
Handles parsing Java model files and converting to BlockBench compatible JSON
"""

import json
import re
from dataclasses import dataclass, asdict
from typing import List, Dict, Tuple, Optional
from pathlib import Path


@dataclass
class Box:
    """Represents a cube/box in the model"""
    u: int
    v: int
    offset_x: float
    offset_y: float
    offset_z: float
    width: float
    height: float
    depth: float


@dataclass
class ModelPart:
    """Represents a ModelRenderer part"""
    name: str
    box: Box
    rotation_point: Tuple[float, float, float]
    rotation: Tuple[float, float, float] = (0.0, 0.0, 0.0)

    def to_element(self, index: int, uuid: str) -> Dict:
        """Convert to BBModel element format (BlockBench 5.0)"""
        x, y, z = self.rotation_point

        # Apply addBox offset to get the actual box position
        from_x = x + self.box.offset_x
        from_y = y + self.box.offset_y
        from_z = z + self.box.offset_z

        to_x = from_x + self.box.width
        to_y = from_y + self.box.height
        to_z = from_z + self.box.depth

        # Calculate UV coordinates for each face
        u_max = self.box.u + self.box.width
        v_max = self.box.v + self.box.height
        u_depth = self.box.u + self.box.depth
        v_depth = self.box.v + self.box.depth

        element = {
            "name": self.name,
            "box_uv": False,
            "render_order": "default",
            "rescale": False,
            "locked": False,
            "shade": True,
            "light_emission": 0,
            "export": True,
            "scope": 0,
            "allow_mirror_modeling": True,
            "from": [from_x, from_y, from_z],
            "to": [to_x, to_y, to_z],
            "autouv": 1,
            "color": 6,
            "origin": [x, y, z],
            "faces": {
                "north": {"uv": [self.box.u, self.box.v, u_max, v_max]},
                "east": {"uv": [self.box.u, self.box.v, u_depth, v_max]},
                "south": {"uv": [self.box.u, self.box.v, u_max, v_max]},
                "west": {"uv": [self.box.u, self.box.v, u_depth, v_max]},
                "up": {"uv": [self.box.u, self.box.v, u_max, v_depth]},
                "down": {"uv": [self.box.u, self.box.v, u_max, v_depth]},
            },
            "type": "cube",
            "uuid": uuid,
        }
        return element


class JavaModelParser:
    """Parses Minecraft Java model files"""

    def __init__(self, java_file: Path):
        self.java_file = Path(java_file)
        self.content = java_file.read_text()
        self.parts: List[ModelPart] = []
        self.texture_width = 64
        self.texture_height = 64
        self.model_name = "Model"

    def parse(self) -> None:
        """Parse the Java model file"""
        self._extract_texture_dimensions()
        self._extract_model_name()
        self._extract_model_parts()

    def _extract_texture_dimensions(self) -> None:
        """Extract texture width and height"""
        width_match = re.search(r'textureWidth\s*=\s*(\d+)', self.content)
        height_match = re.search(r'textureHeight\s*=\s*(\d+)', self.content)

        if width_match:
            self.texture_width = int(width_match.group(1))
        if height_match:
            self.texture_height = int(height_match.group(1))

    def _extract_model_name(self) -> None:
        """Extract the model class name"""
        class_match = re.search(r'public class (\w+)\s+extends\s+ModelBase', self.content)
        if class_match:
            self.model_name = class_match.group(1)

    def _extract_model_parts(self) -> None:
        """Extract all ModelRenderer parts"""
        # Find all ModelRenderer field declarations
        field_pattern = r'ModelRenderer\s+(\w+)\s*;'
        field_names = re.findall(field_pattern, self.content)

        for field_name in field_names:
            part = self._parse_model_part(field_name)
            if part:
                self.parts.append(part)

    def _parse_model_part(self, name: str) -> Optional[ModelPart]:
        """Parse a single ModelRenderer definition"""
        # Pattern to find the initialization block for this part
        # Matches: name = new ModelRenderer(...);
        # followed by name.addBox(...);
        # followed by name.setRotationPoint(...);
        # optionally followed by setRotation(...);

        pattern = rf'{name}\s*=\s*new\s+ModelRenderer\(this,\s*(\d+),\s*(\d+)\);.*?{name}\.addBox\(([^)]+)\);.*?{name}\.setRotationPoint\(([^)]+)\);'
        match = re.search(pattern, self.content, re.DOTALL)

        if not match:
            return None

        u = int(match.group(1))
        v = int(match.group(2))

        # Parse addBox parameters: addBox(offsetX, offsetY, offsetZ, width, height, depth)
        box_params = self._parse_float_params(match.group(3))
        if len(box_params) < 6:
            return None

        offset_x, offset_y, offset_z = box_params[0], box_params[1], box_params[2]
        width, height, depth = box_params[3], box_params[4], box_params[5]

        # Parse setRotationPoint parameters
        rotation_point = self._parse_float_params(match.group(4))
        if len(rotation_point) < 3:
            return None

        # Check for setRotation call
        rotation_pattern = rf'setRotation\({name},\s*([^)]+)\);'
        rotation_match = re.search(rotation_pattern, self.content)

        rotation = (0.0, 0.0, 0.0)
        if rotation_match:
            rotation = tuple(self._parse_float_params(rotation_match.group(1)))

        box = Box(u=u, v=v, offset_x=offset_x, offset_y=offset_y, offset_z=offset_z,
                  width=width, height=height, depth=depth)

        return ModelPart(
            name=name,
            box=box,
            rotation_point=tuple(rotation_point[:3]),
            rotation=rotation,
        )

    @staticmethod
    def _parse_float_params(param_str: str) -> List[float]:
        """Parse comma-separated float parameters"""
        # Remove 'F' suffix and parse
        param_str = param_str.replace('F', '')
        parts = [p.strip() for p in param_str.split(',')]
        try:
            return [float(p) for p in parts if p]
        except ValueError:
            return []


class BBModelGenerator:
    """Generates BBModel JSON from parsed model data"""

    def __init__(self, parser: JavaModelParser):
        self.parser = parser
        self._normalize_coordinates()

    def _normalize_coordinates(self) -> None:
        """Normalize coordinates to model-local space"""
        if not self.parser.parts:
            return

        # Find bounding box of all rotation points
        all_points = [p.rotation_point for p in self.parser.parts]
        min_x = min(p[0] for p in all_points)
        min_y = min(p[1] for p in all_points)
        min_z = min(p[2] for p in all_points)

        # Subtract minimum to normalize to origin
        for part in self.parser.parts:
            x, y, z = part.rotation_point
            part.rotation_point = (x - min_x, y - min_y, z - min_z)

    @staticmethod
    def _generate_uuid() -> str:
        """Generate a simple deterministic UUID-like string"""
        import uuid
        return str(uuid.uuid4())

    def generate(self) -> Dict:
        """Generate complete BBModel JSON structure"""
        elements = []
        outliner = []

        for idx, part in enumerate(self.parser.parts):
            element = part.to_element(idx, self._generate_uuid())
            elements.append(element)
            outliner.append(element["uuid"])

        model = {
            "meta": {
                "format_version": "5.0",
                "model_format": "java_block",
                "box_uv": False,
            },
            "name": self.parser.model_name,
            "parent": "",
            "java_block_version": "1.21.11",
            "ambientocclusion": True,
            "front_gui_light": False,
            "visible_box": [1, 1, 0],
            "variable_placeholders": "",
            "multi_file_ruleset": "",
            "variable_placeholder_buttons": [],
            "unhandled_root_fields": {},
            "resolution": {
                "width": self.parser.texture_width,
                "height": self.parser.texture_height,
            },
            "elements": elements,
            "groups": [],
            "outliner": outliner,
            "textures": [],
        }

        return model

    def save(self, output_path: Path) -> None:
        """Generate and save BBModel JSON to file"""
        model = self.generate()
        output_path.write_text(json.dumps(model, indent=2))


def convert_java_model_to_bbmodel(java_file: str, output_file: str) -> None:
    """Main conversion function"""
    java_path = Path(java_file)
    output_path = Path(output_file)

    if not java_path.exists():
        raise FileNotFoundError(f"Java model file not found: {java_file}")

    # Parse the Java model
    parser = JavaModelParser(java_path)
    parser.parse()

    print(f"Parsed model: {parser.model_name}")
    print(f"Texture: {parser.texture_width}x{parser.texture_height}")
    print(f"Parts found: {len(parser.parts)}")
    for part in parser.parts:
        print(f"  - {part.name}: {part.box.width}x{part.box.height}x{part.box.depth}")

    # Generate BBModel
    generator = BBModelGenerator(parser)
    generator.save(output_path)

    print(f"\nGenerated BBModel: {output_path}")


if __name__ == "__main__":
    import sys

    if len(sys.argv) != 3:
        print("Usage: python converter.py <input.java> <output.json>")
        sys.exit(1)

    convert_java_model_to_bbmodel(sys.argv[1], sys.argv[2])
