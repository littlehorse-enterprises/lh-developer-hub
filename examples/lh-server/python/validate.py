import compileall
import importlib.util
import sys
from pathlib import Path

ROOT = Path(__file__).parent
WORKFLOWS = (
    ("02-workflows", "get_workflow"),
    ("03-variables", "get_workflow"),
    ("04-conditionals", "get_workflow"),
    ("06-threads", "MyWorkflow"),
    ("07-child-workflows", "get_workflows"),
    ("08-exception-handling", "get_workflow"),
    ("09-external-events", "get_workflow"),
    ("10-interrupts", "UnderpantsWorkflow"),
    ("11-correlated-events", "get_workflow"),
    ("16-user-tasks", "get_workflow"),
    ("21-structdefs", "quickstart_wf"),
)


def load_workflow(concept: str, factory: str) -> None:
    directory = ROOT / concept
    sys.path.insert(0, str(directory))
    for module_name in ("tasks", "workflow"):
        sys.modules.pop(module_name, None)

    spec = importlib.util.spec_from_file_location(
        f"workflow_{concept[:2]}", directory / "workflow.py"
    )
    if spec is None or spec.loader is None:
        raise RuntimeError(f"Unable to load {concept}")
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)

    if factory == "MyWorkflow":
        module.MyWorkflow().get_workflow()
    elif factory == "UnderpantsWorkflow":
        module.UnderpantsWorkflow().get_workflow()
    else:
        getattr(module, factory)()
    sys.path.pop(0)


if __name__ == "__main__":
    if not compileall.compile_dir(ROOT, quiet=1):
        raise SystemExit("Python compilation failed")
    for concept, factory in WORKFLOWS:
        load_workflow(concept, factory)
    print("Validated 12 Python concept examples.")
