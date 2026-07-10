export default function renderConfigPanel(root, config, api) {
    root.innerHTML = `
        <section class="test-extension-panel">
            <h1>Test Resource Extension</h1>
            <p>This panel was loaded from the test classpath.</p>
            <pre>${JSON.stringify(config, null, 2)}</pre>
        </section>
    `;
}