import React from 'react';
import { Form, Button, Container, Row, Col } from 'react-bootstrap'
import i18n from './../Language/i18n'

export default function LoginCode(props) {
    return (
        <div>
            <Form>
                <Form.Group>
                    <Form.Text>{i18n.t('login.code-sent')} {props.phone}</Form.Text>
                    <Form.Control
                        aria-label="input code"
                        type="text"
                        name="code"
                        autoComplete="off"
                        autoFocus
                        placeholder={i18n.t('login.input-code')}
                        onChange={(event) => props.handleChange(event)} 
                    />
                </Form.Group>
                <Container>
                    <Row
                        noGutters="true"
                        className="justify-content-center">
                        <Col xs="auto" sm="auto">
                            <Button
                                className="backButton"
                                onClick={(event) => props.handleBackClick(event)}
                                type="button"
                                variant="primary">
                                {i18n.t("buttons.back")}
                            </Button>
                        </Col>
                        <Col xs="auto" sm="auto"> 
                            <Button
                                float="center"
                                variant="primary"
                                type="submit"
                                name="login"
                                onClick={(event) => props.handleNextClick(event)}>
                                    {i18n.t('login.login')}
                            </Button>
                        </Col>
                    </Row>
                </Container>
            </Form>
        </div>
    )
}
