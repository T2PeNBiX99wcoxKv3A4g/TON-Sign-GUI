# coding: utf-8

import typer

propertieName = 'ton-sign.version'

def main(new_version: str):
    typer.echo(f"New version: {new_version}")

    with open('./gradle.properties', 'r+') as file:
        old_text = ""
        new_version = new_version.replace('"', '')
        new_version = new_version[1:]
        old_version: str = ""

        for line in file.readlines():
            start = line.find(propertieName)

            if start < 0:
                old_text += line
                continue

            old_version = line[start + (len(propertieName) + 1):-1]
            typer.echo(f'Old version: {old_version}, New version: {new_version}')
            old_text += line

        new_text = old_text.replace(old_version, new_version)

        file.seek(0)
        file.truncate(0)
        file.write(new_text)
        file.close()

if __name__ == "__main__":
    typer.run(main)