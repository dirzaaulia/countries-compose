import os
from PIL import Image, ImageDraw, ImageFont, ImageFilter

def create_rounded_mask(size, radius):
    mask = Image.new('L', size, 0)
    draw = ImageDraw.Draw(mask)
    draw.rounded_rectangle([(0, 0), size], radius=radius, fill=255)
    return mask

def generate_graphic(locale: str, subtitle: str, description: str, pills_row1: list, pills_row2: list):
    PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    output_dir = os.path.join(PROJECT_ROOT, f"androidApp/src/main/play/listings/{locale}/graphics/feature-graphic")
    os.makedirs(output_dir, exist_ok=True)
    output_path = os.path.join(output_dir, "1.png")

    icon_path = os.path.join(PROJECT_ROOT, f"androidApp/src/main/play/listings/{locale}/graphics/icon/1.png")
    if not os.path.exists(icon_path):
        icon_path = os.path.join(PROJECT_ROOT, "androidApp/src/main/play/listings/en-US/graphics/icon/1.png")

    screenshots_dir = os.path.join(PROJECT_ROOT, f"androidApp/src/main/play/listings/{locale}/graphics/phone-screenshots")
    if not os.path.exists(screenshots_dir):
        screenshots_dir = os.path.join(PROJECT_ROOT, "androidApp/src/main/play/listings/en-US/graphics/phone-screenshots")

    screen_earth = os.path.join(screenshots_dir, "1.png")
    screen_solar = os.path.join(screenshots_dir, "7.png")

    WIDTH = 1024
    HEIGHT = 500

    # 1. Base gradient background (#050508 to #0D121F deep cosmic navy)
    base = Image.new('RGBA', (WIDTH, HEIGHT), (5, 5, 8, 255))
    draw = ImageDraw.Draw(base)
    for y in range(HEIGHT):
        ratio = y / HEIGHT
        r = int(5 * (1 - ratio) + 10 * ratio)
        g = int(5 * (1 - ratio) + 16 * ratio)
        b = int(8 * (1 - ratio) + 32 * ratio)
        draw.line([(0, y), (WIDTH, y)], fill=(r, g, b, 255))

    # 2. Ambient glow behind phones and subtle aurora glow
    glow = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    glow_draw.ellipse([(500, 20), (1020, 480)], fill=(6, 182, 212, 50))   # Cyan #06B6D4
    glow_draw.ellipse([(680, 70), (980, 440)], fill=(59, 130, 246, 45))   # Blue #3B82F6
    glow_draw.ellipse([(40, 20), (320, 280)], fill=(56, 189, 248, 20))    # Subtle top-left branding haze
    glow = glow.filter(ImageFilter.GaussianBlur(95))
    base = Image.alpha_composite(base, glow)

    # 3. Typography setup
    font_bold = "C:/Windows/Fonts/segoeuib.ttf"
    font_semibold = "C:/Windows/Fonts/seguisb.ttf"
    font_regular = "C:/Windows/Fonts/segoeui.ttf"

    title_font = ImageFont.truetype(font_bold, 56)
    sub_font = ImageFont.truetype(font_semibold, 22)
    desc_font = ImageFont.truetype(font_regular, 16)
    pill_font = ImageFont.truetype(font_bold, 13)

    # Left Content Layout
    text_layer = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
    text_draw = ImageDraw.Draw(text_layer)

    LEFT_MARGIN = 56

    # App Icon (68x68 with rounded corner and clean stroke)
    if os.path.exists(icon_path):
        app_icon = Image.open(icon_path).convert('RGBA')
        app_icon = app_icon.resize((68, 68), Image.Resampling.LANCZOS)
        icon_mask = create_rounded_mask((68, 68), 18)
        base.paste(app_icon, (LEFT_MARGIN, 54), icon_mask)

        # Border around icon
        icon_stroke = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        is_draw = ImageDraw.Draw(icon_stroke)
        is_draw.rounded_rectangle([(LEFT_MARGIN, 54), (LEFT_MARGIN + 68, 54 + 68)], radius=18, outline=(255, 255, 255, 60), width=2)
        base = Image.alpha_composite(base, icon_stroke)

    # Brand Title
    text_draw.text((LEFT_MARGIN, 146), "TERRA", font=title_font, fill=(255, 255, 255, 255))

    # Subtitle
    text_draw.text((LEFT_MARGIN, 216), subtitle, font=sub_font, fill=(56, 189, 248, 255))

    # Description
    text_draw.text(
        (LEFT_MARGIN, 254),
        description,
        font=desc_font,
        fill=(148, 163, 184, 255),
        spacing=6
    )

    # Feature Pills
    def draw_pill_row(pills, y_pos):
        cur_x = LEFT_MARGIN
        for label, color in pills:
            bbox = pill_font.getbbox(label)
            pw = bbox[2] - bbox[0] + 28
            ph = 30
            text_draw.rounded_rectangle(
                [(cur_x, y_pos), (cur_x + pw, y_pos + ph)],
                radius=15,
                fill=(color[0], color[1], color[2], 35),
                outline=(color[0], color[1], color[2], 180),
                width=1
            )
            # Dot indicator
            dot_r = 3
            dot_y = y_pos + ph // 2
            text_draw.ellipse(
                [(cur_x + 12 - dot_r, dot_y - dot_r), (cur_x + 12 + dot_r, dot_y + dot_r)],
                fill=(color[0], color[1], color[2], 255)
            )
            text_draw.text((cur_x + 22, y_pos + 6), label, font=pill_font, fill=(color[0], color[1], color[2], 255))
            cur_x += pw + 10

    draw_pill_row(pills_row1, 345)
    draw_pill_row(pills_row2, 388)

    base = Image.alpha_composite(base, text_layer)

    # 4. Render App Screenshots on the right side
    def render_phone_card(screenshot_path, target_width, pos_x, pos_y, shadow_blur=35):
        if not os.path.exists(screenshot_path):
            print(f"Warning: {screenshot_path} not found")
            return
        phone = Image.open(screenshot_path).convert('RGBA')
        aspect = phone.height / phone.width
        target_height = int(target_width * aspect)
        phone_resized = phone.resize((target_width, target_height), Image.Resampling.LANCZOS)

        radius = 28
        mask = create_rounded_mask((target_width, target_height), radius)

        # Shadow
        shadow = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        s_draw = ImageDraw.Draw(shadow)
        s_draw.rounded_rectangle(
            [(pos_x - 6, pos_y + 16), (pos_x + target_width + 6, pos_y + target_height + 26)],
            radius=radius + 6,
            fill=(0, 0, 0, 220)
        )
        shadow = shadow.filter(ImageFilter.GaussianBlur(shadow_blur))
        nonlocal base
        base = Image.alpha_composite(base, shadow)

        # Paste phone screen
        base.paste(phone_resized, (pos_x, pos_y), mask)

        # Bezel border
        bezel = Image.new('RGBA', (WIDTH, HEIGHT), (0, 0, 0, 0))
        b_draw = ImageDraw.Draw(bezel)
        b_draw.rounded_rectangle(
            [(pos_x, pos_y), (pos_x + target_width, pos_y + target_height)],
            radius=radius,
            outline=(255, 255, 255, 55),
            width=2
        )
        base = Image.alpha_composite(base, bezel)

    # Secondary Phone (Behind to the right): Solar System Screen (7.png)
    render_phone_card(screen_solar, target_width=215, pos_x=775, pos_y=60, shadow_blur=35)

    # Primary Phone (Front and center-right): Earth 3D Screen (1.png)
    render_phone_card(screen_earth, target_width=230, pos_x=545, pos_y=30, shadow_blur=42)

    # 5. Save final RGB 1024x500 PNG
    final = base.convert('RGB')
    final.save(output_path, 'PNG', quality=95)
    print(f"Generated feature graphic for [{locale}]: {output_path}")

def main():
    # English (en-US)
    generate_graphic(
        locale="en-US",
        subtitle="3D Earth & Solar System",
        description="Explore Earth in 3D, live satellite orbits,\nplanetary time, and interactive celestial wonders.",
        pills_row1=[
            ("3D PLANET EXPLORER", (56, 189, 248)),
            ("LIVE SATELLITES", (168, 85, 247)),
        ],
        pills_row2=[
            ("SOLAR SYSTEM 3D", (251, 191, 36)),
            ("PLANETARY TIME", (16, 185, 129)),
        ]
    )

    # Indonesian (id)
    generate_graphic(
        locale="id",
        subtitle="Bumi 3D & Tata Surya",
        description="Jelajahi Bumi 3D, orbit satelit langsung,\nwaktu planet, dan keajaiban tata surya interaktif.",
        pills_row1=[
            ("EKSPLORASI BUMI 3D", (56, 189, 248)),
            ("RADAR SATELIT", (168, 85, 247)),
        ],
        pills_row2=[
            ("TATA SURYA 3D", (251, 191, 36)),
            ("WAKTU PLANET", (16, 185, 129)),
        ]
    )

if __name__ == "__main__":
    main()
